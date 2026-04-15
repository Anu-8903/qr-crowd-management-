// Shared helpers for vanilla pages
window.APP = {
  API_BASE: "http://localhost:8081",

  async json(url, options = {}) {
    const res = await fetch(url, options);
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      const msg = data && data.message ? data.message : "Request failed";
      throw new Error(msg);
    }
    return data;
  },

  levelFromZone(z) {
    const lvl = (z.crowdLevel || "").toUpperCase();
    return ["LOW","MEDIUM","HIGH","FULL"].includes(lvl) ? lvl : "LOW";
  },

  computeLevel(count, cap) {
    if (!cap || cap <= 0) return "LOW";
    const pct = (count * 100) / cap;
    if (pct >= 100) return "FULL";
    if (pct > 80) return "HIGH";
    if (pct >= 50) return "MEDIUM";
    return "LOW";
  }
};

