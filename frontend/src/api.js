const BASE = import.meta.env.VITE_API_URL ?? "http://localhost:8080/arenahub";

async function send(method, path, body) {
  const res = await fetch(BASE + path, {
    method,
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    const fields = data?.fields?.map((f) => f.message).join(" • ");
    throw new Error(fields ? `${data.message}: ${fields}` : data?.message ?? "Erro inesperado");
  }
  return data;
}

// Baixa um arquivo do back-end (exportação JSON)
export async function download(path, fallbackName) {
  const res = await fetch(BASE + path);
  if (!res.ok) throw new Error("Não foi possível exportar os dados");
  const cd = res.headers.get("Content-Disposition") ?? "";
  const match = cd.match(/filename\*=UTF-8''([^;]+)/i);
  const name = match ? decodeURIComponent(match[1]) : fallbackName;
  const url = URL.createObjectURL(await res.blob());
  const a = document.createElement("a");
  a.href = url;
  a.download = name;
  a.click();
  URL.revokeObjectURL(url);
}

export const api = {
  teams: {
    list: () => send("GET", "/teams"),
    create: (b) => send("POST", "/teams", b),
    update: (id, b) => send("PUT", `/teams/${id}`, b),
    remove: (id) => send("DELETE", `/teams/${id}`),
  },
  tournaments: {
    list: () => send("GET", "/tournaments"),
    get: (id) => send("GET", `/tournaments/${id}`),
    create: (b) => send("POST", "/tournaments", b),
    update: (id, b) => send("PUT", `/tournaments/${id}`, b),
    remove: (id) => send("DELETE", `/tournaments/${id}`),
  },
  registrations: {
    list: (tid) => send("GET", `/registrations/${tid}`),
    add: (tid, teamId) => send("POST", `/registrations/tournament/${tid}`, { teamId }),
    remove: (tid, teamId) => send("DELETE", `/registrations/${tid}/teams/${teamId}`),
  },
  matches: {
    start: (tid) => send("POST", `/matches/tournament/${tid}/start`),
    list: (tid) => send("GET", `/matches/tournament/${tid}`),
    classification: (tid) => send("GET", `/matches/tournament/${tid}/classification`),
    result: (id, homeTeamScore, awayTeamScore) =>
      send("PUT", `/matches/${id}/result`, { homeTeamScore, awayTeamScore }),
  },
};
