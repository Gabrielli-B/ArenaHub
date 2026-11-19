import { NavLink, Route, Routes } from "react-router-dom";
import { Home, Trophy, Users } from "lucide-react";
import Dashboard from "./pages/Dashboard.jsx";
import Tournaments from "./pages/Tournaments.jsx";
import TournamentDetail from "./pages/TournamentDetail.jsx";
import Teams from "./pages/Teams.jsx";

const links = [
  { to: "/", label: "Visão Geral", icon: Home },
  { to: "/tournaments", label: "Torneios", icon: Trophy },
  { to: "/teams", label: "Equipes", icon: Users },
];

export default function App() {
  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="logo"><Trophy size={30} /><span>Arena<b>Hub</b></span></div>
        <nav>
          {links.map(({ to, label, icon: Icon }) => (
            <NavLink key={to} to={to} end={to === "/"} className="nav">
              <Icon size={18} /> {label}
            </NavLink>
          ))}
        </nav>
      </aside>
      <main className="content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/tournaments" element={<Tournaments />} />
          <Route path="/tournaments/:id" element={<TournamentDetail />} />
          <Route path="/teams" element={<Teams />} />
        </Routes>
      </main>
    </div>
  );
}

