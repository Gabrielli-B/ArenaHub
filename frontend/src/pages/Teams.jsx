import { useEffect, useState } from "react";
import { Download, Pencil, Plus, Trash2, Users } from "lucide-react";
import { api, download } from "../api.js";
import { Alert, Empty, Modal } from "../components.jsx";

function TeamForm({ team, onClose, onSaved, onError }) {
  const [form, setForm] = useState({ name: team?.name ?? "", teamAcronym: team?.teamAcronym ?? "", city: team?.city ?? "" });
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    try {
      team ? await api.teams.update(team.id, form) : await api.teams.create(form);
      onSaved();
    } catch (err) {
      onError(err.message);
      onClose();
    }
  };

  return (
    <Modal title={team ? "Editar Equipe" : "Nova Equipe"} onClose={onClose}>
      <form onSubmit={submit}>
        <label>Nome *<input required value={form.name} onChange={set("name")} placeholder="Ex: Dragões" /></label>
        <label>Sigla * (2 a 5 letras)<input required value={form.teamAcronym} onChange={set("teamAcronym")} maxLength={5} placeholder="DRG" /></label>
        <label>Cidade<input value={form.city} onChange={set("city")} /></label>
        <div className="actions">
          <button type="button" className="btn outline" onClick={onClose}>Cancelar</button>
          <button className="btn">Salvar</button>
        </div>
      </form>
    </Modal>
  );
}

export default function Teams() {
  const [teams, setTeams] = useState([]);
  const [modal, setModal] = useState(null);
  const [error, setError] = useState("");

  const load = () => api.teams.list().then(setTeams).catch((e) => setError(e.message));
  useEffect(() => { load(); }, []);

  const remove = async (t) => {
    if (!confirm(`Excluir a equipe "${t.name}"?`)) return;
    try { await api.teams.remove(t.id); load(); } catch (e) { setError(e.message); }
  };

  return (
    <>
      <header className="page-head">
        <div><h1><Users /> Equipes</h1><p className="muted">Cadastre e gerencie as equipes da plataforma.</p></div>
        <div className="actions">
          <button className="btn outline" onClick={() => download("/export/teams", "equipes.json").catch((e) => setError(e.message))}>
            <Download size={16} /> Exportar JSON
          </button>
          <button className="btn" onClick={() => setModal("new")}><Plus size={16} /> Nova Equipe</button>
        </div>
      </header>
      <Alert msg={error} onClose={() => setError("")} />

      <div className="card">
        {teams.length === 0 ? <Empty>Nenhuma equipe cadastrada.</Empty> : (
          <table>
            <thead><tr><th>Nome</th><th>Sigla</th><th>Cidade</th><th /></tr></thead>
            <tbody>
              {teams.map((t) => (
                <tr key={t.id}>
                  <td>{t.name}</td><td>{t.teamAcronym}</td><td>{t.city || "—"}</td>
                  <td className="right">
                    <button className="icon" onClick={() => setModal(t)}><Pencil size={16} /></button>
                    <button className="icon danger" onClick={() => remove(t)}><Trash2 size={16} /></button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {modal && (
        <TeamForm
          team={modal === "new" ? null : modal}
          onClose={() => setModal(null)}
          onSaved={() => { setModal(null); load(); }}
          onError={setError}
        />
      )}
    </>
  );
}
