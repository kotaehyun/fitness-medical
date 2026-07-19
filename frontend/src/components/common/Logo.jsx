import { Activity } from 'lucide-react';
import { Link } from 'react-router-dom';
export function Logo({ compact = false }) {
  return (
    <Link to="/" className="logo" aria-label="Fitness Medical 홈">
      <span className="logo-mark">
        <Activity size={20} />
      </span>
      {!compact && (
        <span>
          Fitness <b>Medical</b>
        </span>
      )}
    </Link>
  );
}
