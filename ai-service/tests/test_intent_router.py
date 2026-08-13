from app.services.intent_router import AskIntent, route_query

def test_routes_diagnosis_to_blocked():
    assert route_query("저 당뇨인가요? 진단해주세요") is AskIntent.BLOCKED


def test_routes_lifestyle_to_lifestyle():
    assert route_query("잠은 어떻게 자면 좋나요?") is AskIntent.LIFESTYLE
