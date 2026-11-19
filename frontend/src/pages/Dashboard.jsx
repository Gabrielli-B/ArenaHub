import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { CalendarCheck, CheckCircle2, Trophy, Users } from "lucide-react";
import { api } from "../api.js";
import { Alert, Badge, Empty, MODALITY } from "../components.jsx";

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    (async () => {
      try {
        const [tournaments, teams] = await Promise.all([api.tournaments.list(), api.teams.list()]);
        const started = tournaments.filter((t) => t.status !== "OPEN");
        const all = (await Promise.all(started.map((t) => api.matches.list(t.id)))).flat();
        setData({
          tournaments,
          teams,
          played: all.filter((m) => m.matchStatus === "FINISHED").length,
          next: all.filter((m) => m.matchStatus === "SCHEDULED").slice(0, 4),
        });
      } catch (e) {
        setError(e.message);
      }
    })();
  }, []);

  if (!data) return <Alert msg={error || "Carregando..."} />;
  const { tournaments, teams, played, next } = data;
  const featured = tournaments.find((t) => t.status === "IN_PROGRESS") ?? tournaments[0];

  const stats = [
    { icon: Trophy, value: tournaments.filter((t) => t.status === "IN_PROGRESS").length, label: "Torneios Ativos" },
    { icon: Users, value: teams.length, label: "Equipes Cadastradas" },
    { icon: CalendarCheck, value: played, label: "Partidas Realizadas" },
    { icon: CheckCircle2, value: tournaments.filter((t) => t.status === "OPEN").length, label: "Inscrições em Aberto" },
  ];

  return (
    <>
      <section className="hero">
        <h1>Bem-vinda ao <span>ArenaHub!</span></h1>
        <p>Gerencie seus torneios de forma simples, rápida e eficiente.</p>
      </section>

      <div className="stats">
        {stats.map(({ icon: Icon, value, label }) => (
          <div className="card stat" key={label}>
            <Icon size={26} className="accent" />
            <strong>{value}</strong>
            <span>{label}</span>
          </div>
        ))}
      </div>

      <div className="grid2">
        <div className="card">
          <h3>Torneio em Destaque</h3>
          {featured ? (
            <div className="featured">
              <div className="thumb"><Trophy size={40} /></div>
              <div>
                <h4>{featured.name}</h4>
                <Badge status={featured.status} />
                <p className="muted">{MODALITY[featured.modality]} · {featured.registrationCount}/{featured.teamLimit} equipes</p>
                <Link className="btn outline" to={`/tournaments/${featured.id}`}>Ver detalhes</Link>
              </div>
            </div>
          ) : <Empty>Nenhum torneio ainda.</Empty>}
        </div>

        <div className="card">
          <h3>Próximas Partidas</h3>
          {next.length === 0 ? <Empty>Nenhuma partida agendada.</Empty> :
            next.map((m) => (
              <div className="next" key={m.id}>
                <span>{m.homeTeam.name}</span><small>x</small><span>{m.awayTeam.name}</span>
                <em>Rodada {m.round}</em>
              </div>
            ))}
        </div>
      </div>
    </>
  );
}
