import { useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, Download, Play, Trash2 } from "lucide-react";
import { api, download } from "../api.js";
import { Alert, Badge, Empty, MODALITY } from "../components.jsx";

function MatchRow({ match, editable, onSave }) {
  const done = match.matchStatus === "FINISHED";
  const [h, setH] = useState(done ? match.homeTeamScore : "");
  const [a, setA] = useState(done ? match.awayTeamScore : "");

  return (
    <div className="match">
      <span className="home">{match.homeTeam.name}</span>
      {editable ? (
        <div className="score-box">
          <input type="number" min="0" value={h} onChange={(e) => setH(e.target.value)} />
          <b>x</b>
          <input type="number" min="0" value={a} onChange={(e) => setA(e.target.value)} />
        </div>
      ) : (
        <b className="score">{done ? `${match.homeTeamScore} x ${match.awayTeamScore}` : "x"}</b>
      )}
      <span className="away">{match.awayTeam.name}</span>
      {editable && (
        <button className="btn sm" disabled={h === "" || a === ""} onClick={() => onSave(match.id, Number(h), Number(a))}>
          {done ? "Corrigir" : "Lançar"}
        </button>
      )}
    </div>
  );
}

export default function TournamentDetail() {
  const { id } = useParams();
  const [t, setT] = useState(null);
  const [regs, setRegs] = useState([]);
  const [table, setTable] = useState([]);
  const [matches, setMatches] = useState([]);
  const [teams, setTeams] = useState([]);
  const [tab, setTab] = useState(null);
  const [teamId, setTeamId] = useState("");
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    try {
      const [tt, r, c, m, all] = await Promise.all([
        api.tournaments.get(id), api.registrations.list(id), api.matches.classification(id),
        api.matches.list(id), api.teams.list(),
      ]);
      setT(tt); setRegs(r); setTable(c); setMatches(m); setTeams(all);
    } catch (e) {
      setError(e.message);
    }
  }, [id]);

  useEffect(() => { load(); }, [load]);

  const run = async (fn) => {
    setError("");
    try { await fn(); await load(); } catch (e) { setError(e.message); }
  };

  if (!t) return <Alert msg={error || "Carregando..."} />;

  const current = tab ?? (t.status === "OPEN" ? "teams" : "table");
  const open = t.status === "OPEN";
  const inProgress = t.status === "IN_PROGRESS";
  const registeredIds = new Set(regs.map((r) => r.team.id));
  const available = teams.filter((x) => !registeredIds.has(x.id));
  const rounds = matches.reduce((acc, m) => { (acc[m.round] ??= []).push(m); return acc; }, {});

  return (
    <>
      <Link to="/tournaments" className="back"><ArrowLeft size={16} /> Voltar para torneios</Link>
      <header className="page-head">
        <div>
          <h1>{t.name} <Badge status={t.status} /></h1>
          <p className="muted">{MODALITY[t.modality]} · {t.registrationCount}/{t.teamLimit} equipes</p>
        </div>
        <div className="actions">
          {open && <button className="btn" onClick={() => run(() => api.matches.start(id))}><Play size={16} /> Iniciar torneio</button>}
          {t.status === "FINISHED" && (
            <button className="btn outline" onClick={() => download(`/export/tournament/${id}`, `torneio-${id}.json`).catch((e) => setError(e.message))}>
              <Download size={16} /> Exportar JSON
            </button>
          )}
        </div>
      </header>
      <Alert msg={error} onClose={() => setError("")} />

      <div className="tabs">
        {[["table", "Classificação"], ["matches", "Partidas"], ["teams", "Equipes"]].map(([k, label]) => (
          <button key={k} className={current === k ? "active" : ""} onClick={() => setTab(k)}>{label}</button>
        ))}
      </div>

      {current === "table" && (
        <div className="card">
          <table>
            <thead><tr><th>#</th><th>Equipe</th><th>Pts</th><th>J</th><th>V</th><th>E</th><th>D</th><th>GP</th><th>GC</th><th>Saldo</th></tr></thead>
            <tbody>
              {table.map((r) => (
                <tr key={r.team.id} className={r.position === 1 && r.games > 0 ? "leader" : ""}>
                  <td>{r.position}</td><td>{r.team.name}</td><td><b>{r.points}</b></td><td>{r.games}</td>
                  <td>{r.wins}</td><td>{r.draws}</td><td>{r.losses}</td><td>{r.scoreFor}</td>
                  <td>{r.scoreAgainst}</td><td>{r.scoreDifference > 0 ? `+${r.scoreDifference}` : r.scoreDifference}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {table.length === 0 && <Empty>Nenhuma equipe inscrita.</Empty>}
        </div>
      )}

      {current === "matches" && (
        matches.length === 0 ? <Empty>As partidas são geradas ao iniciar o torneio.</Empty> :
        Object.entries(rounds).map(([round, list]) => (
          <div className="card" key={round}>
            <h3>Rodada {round}</h3>
            {list.map((m) => (
              <MatchRow key={`${m.id}-${m.homeTeamScore}-${m.awayTeamScore}`} match={m} editable={inProgress}
                onSave={(mid, h, a) => run(() => api.matches.result(mid, h, a))} />
            ))}
          </div>
        ))
      )}

      {current === "teams" && (
        <div className="card">
          {open && (
            <div className="add-team">
              <select value={teamId} onChange={(e) => setTeamId(e.target.value)}>
                <option value="">Selecione uma equipe para inscrever</option>
                {available.map((x) => <option key={x.id} value={x.id}>{x.name} ({x.teamAcronym})</option>)}
              </select>
              <button className="btn" disabled={!teamId}
                onClick={() => run(async () => { await api.registrations.add(id, Number(teamId)); setTeamId(""); })}>
                Inscrever
              </button>
            </div>
          )}
          {regs.length === 0 ? <Empty>Nenhuma equipe inscrita.</Empty> : (
            <table>
              <thead><tr><th>Equipe</th><th>Sigla</th><th>Cidade</th><th /></tr></thead>
              <tbody>
                {regs.map((r) => (
                  <tr key={r.id}>
                    <td>{r.team.name}</td><td>{r.team.teamAcronym}</td><td>{r.team.city || "—"}</td>
                    <td className="right">
                      {open && <button className="icon danger" onClick={() => run(() => api.registrations.remove(id, r.team.id))}><Trash2 size={16} /></button>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </>
  );
}
