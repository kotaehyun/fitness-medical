"""
[공부/면접] Chunk 분할.

Q. 왜 overlap을 두나?
A. Chunk 경계에서 문맥이 끊기면 검색 품질이 떨어진다.
   앞 조각 끝을 다음 조각 앞에 조금 겹친다.

지금은 토큰이 아니라 글자 수 기준(학습용)이다.

Q. chunk_size / overlap 검증은?
A. size<=0 이면 무한 루프·빈 조각. overlap>=size 이면 start가 안 나아가 무한 루프.
"""

def split_text(
    text: str,
    *,
    chunk_size: int = 500,
    overlap: int = 50,
) -> list[str]:
    if chunk_size <= 0:
        raise ValueError("chunk_size는 1 이상이어야 합니다.")
    if overlap < 0 or overlap >= chunk_size:
        raise ValueError("overlap은 0 이상이고 chunk_size보다 작아야 합니다.")

    normalized = text.strip()
    if not normalized:
        return []
    if len(normalized) <= chunk_size:
        return [normalized]

    chunks: list[str] = []
    start = 0
    length = len(normalized)

    while start < length:
        end = min(start + chunk_size, length)
        piece = normalized[start:end].strip()
        if piece:
            chunks.append(piece)
        if end >= length:
            break
        next_start = end - overlap
        if next_start <= start:
            next_start = end
        start = next_start
    return chunks
