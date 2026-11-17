import { useEffect, useState } from "react";
import api from "../api";
import { useAuth } from "../Context/AuthContext";

export default function Dashboard() {
  const { user, logout } = useAuth();
  const [links, setLinks] = useState([]);
  const [longUrl, setLongUrl] = useState("");
  const [customAlias, setCustomAlias] = useState("");
  const [error, setError] = useState("");

  // Fetch links belonging ONLY to the logged-in user.
  // The backend enforces this via the JWT-derived user in
  // UrlController — the frontend never sends a userId, so
  // there's no way to request someone else's links even by
  // tampering with the request.

  useEffect(() => {
  fetchLinks();

  const interval = setInterval(() => {
    fetchLinks();
  }, 5000);

  return () => clearInterval(interval);
}, []);
  const fetchLinks = async () => {
  try {
    const res = await api.get("/urls");
    setLinks(res.data);
  } catch (err) {
    console.error("Failed to fetch links:", err);
    setError("Could not load your links.");
  }
};

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await api.post("/urls", {
        longUrl,
        customAlias: customAlias || null,
      });
      setLongUrl("");
      setCustomAlias("");
     await fetchLinks();
    } catch (err) {
      // Backend returns 403 PlanRestrictionException for free users
      // trying a custom alias — surfaced here, not silently ignored
      setError(err.response?.data?.message || "Failed to create link");
    }
  };

  const totalClicks = links.reduce((sum, l) => sum + (l.clickCount || 0), 0);

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <aside className="w-64 bg-white border-r border-gray-200 p-6 flex flex-col justify-between">
        <div>
          <h1 className="text-xl font-bold text-gray-900 mb-8">LinkFast</h1>
          <nav className="space-y-1">
            <SidebarItem label="Dashboard" active />
            <SidebarItem label="Links" />
            {user?.planType === "PREMIUM" && <SidebarItem label="Analytics" />}
            {user?.role === "ADMIN" && <SidebarItem label="Admin Panel" />}
          </nav>
        </div>
        <div className="border-t border-gray-100 pt-4">
          <p className="text-sm font-medium text-gray-900">{user?.email}</p>
          <p className="text-xs text-gray-400 mb-2">{user?.planType} plan</p>
          <button
            onClick={logout}
            className="text-sm text-red-500 hover:text-red-600"
          >
            Log out
          </button>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 p-8">
        <h2 className="text-2xl font-semibold text-gray-900 mb-1">
          Good morning, {user?.email?.split("@")[0]}
        </h2>
        <p className="text-gray-500 mb-6">Manage and track your links</p>

        {/* Stat cards */}
        <div className="grid grid-cols-3 gap-4 mb-8">
          <StatCard label="Total Links" value={links.length} />
          <StatCard label="Total Clicks" value={totalClicks} />
          <StatCard label="Plan" value={user?.planType || "—"} />
        </div>

        {/* Create link form */}
        <div className="bg-white rounded-xl border border-gray-200 p-6 mb-8">
          <h3 className="font-medium text-gray-900 mb-4">Create a new link</h3>
          <form onSubmit={handleCreate} className="flex gap-3 flex-wrap">
            <input
              type="url"
              placeholder="https://your-long-url.com/..."
              value={longUrl}
              onChange={(e) => setLongUrl(e.target.value)}
              required
              className="flex-1 min-w-[240px] border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-black"
            />
            <input
              type="text"
              placeholder="Custom alias (Premium)"
              value={customAlias}
              onChange={(e) => setCustomAlias(e.target.value)}
              disabled={user?.planType === "FREE"}
              className="w-56 border border-gray-200 rounded-lg px-3 py-2 text-sm disabled:bg-gray-50 disabled:text-gray-400 focus:outline-none focus:ring-2 focus:ring-black"
            />
            <button
              type="submit"
              className="bg-black text-white rounded-lg px-5 py-2 text-sm font-medium hover:bg-gray-800"
            >
              Shorten
            </button>
          </form>
          {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
        </div>

        {/* Links table */}
        <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-left">
              <tr>
                <th className="px-4 py-3 font-medium">Short URL</th>
                <th className="px-4 py-3 font-medium">Original URL</th>
                <th className="px-4 py-3 font-medium">Clicks</th>
                <th className="px-4 py-3 font-medium">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {links.map((link) => (
                <tr key={link.id}>
                  <td className="px-4 py-3">
                    <a
                      href={link.shortUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="text-blue-600 hover:underline"
                    >
                      {link.shortUrl}
                    </a>
                  </td>
                  <td className="px-4 py-3 text-gray-500 truncate max-w-xs">
                    {link.longUrl}
                  </td>
                  <td className="px-4 py-3">{link.clickCount}</td>
                  <td className="px-4 py-3">
                    <span
                      className={`text-xs px-2 py-1 rounded-full ${
                        link.status === "ACTIVE"
                          ? "bg-green-50 text-green-600"
                          : "bg-gray-100 text-gray-500"
                      }`}
                    >
                      {link.status}
                    </span>
                  </td>
                </tr>
              ))}
              {links.length === 0 && (
                <tr>
                  <td colSpan={4} className="px-4 py-8 text-center text-gray-400">
                    No links yet — create your first one above.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}

function SidebarItem({ label, active }) {
  return (
    <div
      className={`px-3 py-2 rounded-lg text-sm cursor-pointer ${
        active
          ? "bg-gray-100 text-gray-900 font-medium"
          : "text-gray-500 hover:bg-gray-50"
      }`}
    >
      {label}
    </div>
  );
}

function StatCard({ label, value }) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5">
      <p className="text-sm text-gray-500 mb-1">{label}</p>
      <p className="text-2xl font-semibold text-gray-900">{value}</p>
    </div>
  );
}