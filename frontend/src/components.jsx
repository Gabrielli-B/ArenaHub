import { X } from "lucide-react";

export const STATUS = {
  OPEN: ["Aberto", "blue"],
  IN_PROGRESS: ["Em Andamento", "green"],
  FINISHED: ["Finalizado", "gray"],
  CANCELED: ["Cancelado", "red"],
};

export const MODALITY = {
  VOLLEYBALL: "Vôlei", SOCCER: "Futebol", FUTSAL: "Futsal", BASKETBALL: "Basquete",
  CANASTA: "Canastra", TRUCO: "Truco", UNO: "Uno", CHESS: "Xadrez", CHECKERS: "Damas",
};

export const Badge = ({ status }) => (
  <span className={`badge ${STATUS[status][1]}`}>{STATUS[status][0]}</span>
);

export const Alert = ({ msg, onClose }) =>
  msg ? <div className="alert" onClick={onClose}>{msg} <small>(clique para fechar)</small></div> : null;

export const Empty = ({ children }) => <div className="empty">{children}</div>;

export function Modal({ title, onClose, children }) {
  return (
    <div className="overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-head">
          <h3>{title}</h3>
          <button className="icon" onClick={onClose}><X size={18} /></button>
        </div>
        {children}
      </div>
    </div>
  );
}