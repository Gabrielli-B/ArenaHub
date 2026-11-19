import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Pencil, Plus, Trash2, Trophy } from "lucide-react";
import { api } from "../api.js";
import { Alert, Badge, Empty, MODALITY, Modal, STATUS } from "../components.jsx";

const FILTERS = [["ALL", "Todos"], ["OPEN", "Aberto"], ["IN_PROGRESS", "Em Andamento"], ["FINISHED", "Finalizado"]];

function TournamentForm({ tournament, onClose, onSaved, onError }) {
  const [form, setForm] = useState({
    name: tournament?.name ?? "",
    modality: tournament?.modality ?? "",
    teamLimit: tournament?.teamLimit ?? "",
  });
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    const body = { ...form, teamLimit: Number(form.teamLimit) };
    try {
      tournament ? await api.tournaments.update(tournament.id, body) : await api.tournaments.create(body);
      onSaved();
    } catch (err) {
      onError(err.message);
      onClose();
    }
  };

  return (
    <Modal title={tournament ? "Editar Torneio" : "Novo Torneio"} onClose={onClose}>
      <form onSubmit={submit}>
        <label>Nome do Torneio *<input required value={form.name} onChange={set("name")} placeholder="Ex: Campeonato de Verão" /></label>
        <label>Modalidade *
          <select required value={form.modality} onChange={set("modality")}>
            <option value="">Selecione a modalidade</option>
            {Object.entries(MODALITY).map(([k, v]) => <option key={k} value={k}>{v}</option>)}
          </select>
        </label>
        <label>Quantidade de Times (4 a 10) *<input required type="number" min="4" max="10" value={form.teamLimit} onChange={set("teamLimit")} /></label>
        <div className="actions">
          <button type="button" className="btn outline" onClick={onClose}>Cancelar</button>
          <button className="btn">Salvar</button>
        </div>
      </form>
    </Modal>
  );
}

export default function Tournaments() {
  const [list, setList] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [modal, setModal] = useState(null); // null | "new" | tournament
  const [error, setError] = useState("");

  const load = () => api.tournaments.list().then(setList).catch((e) => setError(e.message));
  useEffect(() => { load(); }, []);

  const remove = async (t) => {
    if (!confirm(`Excluir o torneio "${t.name}"?`)) return;
    try { await api.tournaments.remove(t.id); load(); } catch (e) { setError(e.message); }
  };

  const shown = list.filter((t) => filter === "ALL" || t.status === filter);

  return (
    <>
      <header className="page-head">
        <div><h1><Trophy /> Torneios</h1><p className="muted">Confira todos os campeonatos disponíveis na plataforma.</p></div>
        <button className="btn" onClick={() => setModal("new")}><Plus size={16} /> Novo Torneio</button>
      </header>
      <Alert msg={error} onClose={() => setError("")} />

      <div className="tabs pills">
        {FILTERS.map(([k, label]) => (
          <button key={k} className={filter === k ? "active" : ""} onClick={() => setFilter(k)}>{label}</button>
        ))}
      </div>

      {shown.length === 0 && <Empty>Nenhum torneio encontrado.</Empty>}
      {shown.map((t) => (
        <div className="card row" key={t.id}>
          <div className="thumb"><Trophy size={30} /></div>
          <div className="grow">
            <h4>{t.name}</h4>
            <p className="muted">{MODALITY[t.modality]} · {t.registrationCount}/{t.teamLimit} equipes</p>
          </div>
          <Badge status={t.status} />
          {t.status === "OPEN" && (
            <>
              <button className="icon" title="Editar" onClick={() => setModal(t)}><Pencil size={16} /></button>
              <button className="icon danger" title="Excluir" onClick={() => remove(t)}><Trash2 size={16} /></button>
            </>
          )}
          <Link className="btn outline" to={`/tournaments/${t.id}`}>Ver detalhes</Link>
        </div>
      ))}

      {modal && (
        <TournamentForm
          tournament={modal === "new" ? null : modal}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); load(); }}
          onError={setError}
        />
      )}
    </>
  );
}
