/**
 * [공부/면접] 담당 연결 텍스트 1:1
 *
 * Q. 왜 웹소켓이 없나?
 * A. 학습용 폴링. 상대 목록·메시지는 REST. 진단·협진 UI가 아니다.
 *
 * Q. peerAccountId는?
 * A. Account PK. 담당이 아니면 서버가 403.
 */
import { MessagesSquare } from 'lucide-react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { AppShell } from '../../components/layout/AppShell';
import { StateView } from '../../components/common/StateView';
import { useAuthSession } from '../../hooks/useAuthSession';
import { healthService } from '../../services/healthService';
import { memberNav } from '../member/MemberDashboard';
import { professionalNav } from '../professional/ProfessionalDashboard';

/**
 * @param {{ professional?: boolean }} props
 */
export function MessagesPage({ professional = false }) {
  const { account, demoRole } = useAuthSession();
  const pendingVerification =
    professional && !demoRole && account?.professionalVerified === false;
  const nav = professional ? professionalNav : memberNav;
  const queryClient = useQueryClient();
  const [peerId, setPeerId] = useState(null);
  const [draft, setDraft] = useState('');

  const peersQuery = useQuery({
    queryKey: ['messages', 'peers', account?.id],
    queryFn: healthService.getChatPeers,
    enabled: !pendingVerification && Boolean(account?.id),
  });

  useEffect(() => {
    const peers = peersQuery.data ?? [];
    if (!peers.length) {
      return;
    }
    const valid = peers.some((peer) => peer.accountId === peerId);
    if (!valid) {
      setPeerId(peers[0].accountId);
    }
  }, [peerId, peersQuery.data]);

  const myAccountId = account?.id;
  const messagesQuery = useQuery({
    queryKey: ['messages', 'thread', myAccountId, peerId],
    queryFn: () => healthService.getChatMessages(peerId),
    enabled:
      peerId != null &&
      myAccountId != null &&
      peerId !== myAccountId &&
      !pendingVerification,
    refetchInterval: 4000,
  });

  const sendMutation = useMutation({
    mutationFn: (/** @type {string} */ body) =>
      healthService.sendChatMessage(peerId, body),
    onSuccess: () => {
      setDraft('');
      queryClient.invalidateQueries({ queryKey: ['messages', 'thread', myAccountId, peerId] });
    },
  });

  if (pendingVerification) {
    return (
      <AppShell nav={nav} professional={professional}>
        <StateView
          type="empty"
          message="전문직 인증 대기 중입니다. 승인 후 담당 회원과 대화할 수 있습니다."
        />
      </AppShell>
    );
  }

  if (peersQuery.isLoading) {
    return (
      <AppShell nav={nav} professional={professional}>
        <StateView type="loading" message="대화 상대를 불러오는 중입니다." />
      </AppShell>
    );
  }

  if (peersQuery.isError) {
    return (
      <AppShell nav={nav} professional={professional}>
        <StateView type="error" message="대화 상대를 불러오지 못했습니다." />
      </AppShell>
    );
  }

  const peers = peersQuery.data ?? [];
  const selected = peers.find((peer) => peer.accountId === peerId);

  return (
    <AppShell nav={nav} professional={professional}>
      <div className="page-head">
        <div>
          <span className="page-kicker">MESSAGES</span>
          <h1>메시지</h1>
          <p>담당으로 연결된 상대와만 텍스트로 대화합니다. 진단·처방이 아닙니다.</p>
        </div>
        <span className="button secondary compact">
          <MessagesSquare />
          {peers.length}명
        </span>
      </div>
      {peers.length === 0 ? (
        <StateView type="empty" message="담당으로 연결된 대화 상대가 없습니다." />
      ) : (
        <section className="card chat-layout">
          <aside className="chat-peers">
            {peers.map((peer) => (
              <button
                key={peer.accountId}
                type="button"
                className={peer.accountId === peerId ? 'chat-peer active' : 'chat-peer'}
                onClick={() => setPeerId(peer.accountId)}
              >
                <b>{peer.displayName}</b>
                <small>{peer.peerLabel}</small>
              </button>
            ))}
          </aside>
          <div className="chat-thread">
            <div className="chat-thread-head">
              <b>{selected?.displayName}</b>
              <small>{selected?.peerLabel}</small>
            </div>
            <div className="chat-log">
              {messagesQuery.isLoading ? (
                <p className="chat-hint">메시지를 불러오는 중입니다.</p>
              ) : (messagesQuery.data ?? []).length === 0 ? (
                <p className="chat-hint">아직 메시지가 없습니다. 첫 인사를 남겨 보세요.</p>
              ) : (
                (messagesQuery.data ?? []).map((message) => (
                  <article
                    key={message.id}
                    className={
                      message.senderAccountId === myAccountId ? 'chat-bubble mine' : 'chat-bubble'
                    }
                  >
                    <small>{message.senderDisplayName}</small>
                    <p>{message.body}</p>
                    <time>
                      {new Date(message.createdAt).toLocaleString('ko-KR', {
                        month: 'numeric',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </time>
                  </article>
                ))
              )}
            </div>
            <form
              className="chat-compose"
              onSubmit={(event) => {
                event.preventDefault();
                const body = draft.trim();
                if (!body || peerId == null) {
                  return;
                }
                sendMutation.mutate(body);
              }}
            >
              <input
                value={draft}
                onChange={(event) => setDraft(event.target.value)}
                maxLength={1000}
                placeholder="텍스트만 입력합니다."
              />
              <button type="submit" className="button" disabled={sendMutation.isPending}>
                보내기
              </button>
            </form>
            {sendMutation.isError ? (
              <p className="chat-error">
                {sendMutation.error instanceof Error
                  ? sendMutation.error.message
                  : '메시지를 보내지 못했습니다.'}
              </p>
            ) : null}
          </div>
        </section>
      )}
    </AppShell>
  );
}
