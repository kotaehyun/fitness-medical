/**
 * [공부/면접] 법적·시연 고지 (Disclaimer.jsx)
 *
 * Q. 왜 LoginPage·AppShell 하단에 반복 노출?
 * A. 가상 데이터·비의료 서비스임을 사용자에게 상시 고지.
 *
 * Q. 면접에서 강조할 점?
 * A. health 기록 UI는 lifestyle/coaching — 진단·처방 문구·기능을 피한다.
 */
export function Disclaimer() {
  return (
    <p className="disclaimer">
      본 서비스의 인물과 건강 데이터는 시연용 가상 데이터이며, 의료 진단이나 처방을 제공하지
      않습니다.
    </p>
  );
}
