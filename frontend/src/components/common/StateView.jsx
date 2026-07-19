import { AlertCircle, LoaderCircle } from 'lucide-react';
export function StateView({ type, message }) {
  return (
    <div className="state-view" role={type === 'error' ? 'alert' : 'status'}>
      {type === 'loading' ? <LoaderCircle className="spin" /> : <AlertCircle />}
      <p>{message}</p>
    </div>
  );
}
