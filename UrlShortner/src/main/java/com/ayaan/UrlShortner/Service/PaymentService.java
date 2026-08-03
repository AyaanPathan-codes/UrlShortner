package com.ayaan.UrlShortner.Service;

import com.ayaan.UrlShortner.Entity.Enums.PlanType;
import com.ayaan.UrlShortner.Entity.PaymentOrder;
import com.ayaan.UrlShortner.Entity.Users;
import com.ayaan.UrlShortner.Repo.PaymentOrderRepo;
import com.ayaan.UrlShortner.Repo.UsersRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    private final UsersRepo usersRepo;
    private final PaymentOrderRepo paymentOrderRepo;

    public PaymentService(UsersRepo usersRepo, PaymentOrderRepo paymentOrderRepo) {
        this.usersRepo = usersRepo;
        this.paymentOrderRepo = paymentOrderRepo;
    }

    // ---------- CREATE ORDER ----------

    @Transactional
    public String createOrder(Users user, int amountInPaise) throws Exception {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("receipt", "user_" + user.getId());

        Order order = client.orders.create(options);
        String razorpayOrderId = order.get("id");

        // Save the mapping BEFORE returning to client — this is what the
        // webhook will look up later, since Razorpay won't hand your
        // internal userId back to you reliably.
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setRazorpayOrderId(razorpayOrderId);
        paymentOrder.setUser(user);
        paymentOrder.setAmountInPaise(amountInPaise);
        paymentOrderRepo.save(paymentOrder);

        return order.toString();
    }

    // ---------- VERIFY WEBHOOK + UPGRADE PLAN ----------

    @Transactional
    public void handleWebhook(String payload, String signatureHeader) throws Exception {

        boolean isValid = Utils.verifyWebhookSignature(payload, signatureHeader, webhookSecret);
        if (!isValid) {
            throw new SecurityException("Invalid webhook signature — possible spoofed request");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if (!"payment.captured".equals(eventType)) {
            return; // ignore other event types
        }

        JSONObject paymentEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = paymentEntity.getString("order_id");

        PaymentOrder order = paymentOrderRepo.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException(
                        "No matching order found for: " + razorpayOrderId));

        if ("PAID".equals(order.getStatus())) {
            return; // already processed — webhook can fire more than once, stay idempotent
        }

        order.setStatus("PAID");
        paymentOrderRepo.save(order);

        Users user = order.getUser();
        user.setPlanType(PlanType.PREMIUM);
        usersRepo.save(user);
    }
}