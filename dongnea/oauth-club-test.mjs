import http from "node:http";

const PORT = 3000;

const html = String.raw`<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Dongne Test</title>
  <style>
    :root {
      font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      background: #f4f6f8;
      color: #15171a;
    }

    * {
      box-sizing: border-box;
    }

    body {
      margin: 0;
    }

    button, input {
      font: inherit;
    }

    .top {
      position: sticky;
      top: 0;
      z-index: 10;
      display: grid;
      grid-template-columns: minmax(220px, 360px) 1fr;
      gap: 14px;
      padding: 18px;
      border-bottom: 1px solid #d8dee8;
      background: #ffffff;
    }

    .api {
      height: 52px;
      width: 100%;
      padding: 0 16px;
      border: 2px solid #c8d1de;
      border-radius: 10px;
      font-size: 18px;
    }

    .actions {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
      align-items: center;
    }

    .btn {
      min-height: 52px;
      padding: 0 20px;
      border: 2px solid #c8d1de;
      border-radius: 10px;
      background: #fff;
      color: #15171a;
      cursor: pointer;
      font-size: 18px;
      font-weight: 800;
    }

    .btn.primary {
      border-color: #1d4ed8;
      background: #1d4ed8;
      color: #fff;
    }

    .btn.danger {
      border-color: #e5a3a0;
      color: #b42318;
    }

    .status-card {
      margin: 18px;
      padding: 20px;
      border: 2px solid #d8dee8;
      border-radius: 14px;
      background: #fff;
    }

    .status-card strong {
      display: block;
      margin-bottom: 6px;
      font-size: 22px;
    }

    .status-card p {
      margin: 0;
      color: #4b5563;
      font-size: 17px;
      line-height: 1.5;
    }

    .status-card.error {
      border-color: #f0b4b0;
      background: #fff7f6;
      color: #b42318;
    }

    .layout {
      display: grid;
      grid-template-columns: minmax(340px, 520px) minmax(0, 1fr);
      min-height: calc(100vh - 89px);
    }

    .list {
      border-right: 1px solid #d8dee8;
      background: #fff;
      overflow-y: auto;
      max-height: calc(100vh - 89px);
    }

    .club {
      display: grid;
      grid-template-columns: 88px minmax(0, 1fr);
      gap: 16px;
      width: 100%;
      min-height: 124px;
      padding: 18px;
      border: 0;
      border-bottom: 1px solid #edf0f4;
      border-radius: 0;
      background: #fff;
      cursor: pointer;
      text-align: left;
    }

    .club:hover, .club.active {
      background: #edf4ff;
    }

    .club img {
      width: 88px;
      height: 88px;
      border-radius: 14px;
      object-fit: cover;
      background: #e9edf3;
    }

    .club strong {
      display: block;
      margin: 6px 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 22px;
    }

    .club p {
      margin: 0;
      color: #4b5563;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      font-size: 16px;
      line-height: 1.45;
    }

    .badge {
      display: inline-flex;
      padding: 4px 10px;
      border-radius: 999px;
      background: #e6eefc;
      color: #1d4ed8;
      font-size: 14px;
      font-weight: 800;
    }

    .detail {
      padding: 28px;
      overflow-y: auto;
      max-height: calc(100vh - 89px);
    }

    .detail-card {
      max-width: 860px;
      padding: 24px;
      border: 2px solid #d8dee8;
      border-radius: 16px;
      background: #fff;
    }

    .detail-head {
      display: grid;
      grid-template-columns: 132px minmax(0, 1fr);
      gap: 20px;
      align-items: center;
    }

    .detail-head img {
      width: 132px;
      height: 132px;
      border-radius: 18px;
      object-fit: cover;
      background: #e9edf3;
    }

    h1 {
      margin: 8px 0;
      font-size: 36px;
      letter-spacing: 0;
    }

    h2 {
      margin: 0 0 12px;
      font-size: 24px;
    }

    p {
      line-height: 1.65;
    }

    .section {
      margin-top: 22px;
      padding-top: 22px;
      border-top: 1px solid #e4e8ee;
    }

    .section p {
      color: #343a44;
      font-size: 18px;
    }

    details summary {
      cursor: pointer;
      font-size: 24px;
    }

    .recruitment {
      padding: 16px;
      border: 1px solid #d8dee8;
      border-radius: 12px;
      margin-top: 12px;
      background: #fafbfc;
    }

    .recruitment strong {
      display: block;
      font-size: 20px;
      margin-bottom: 4px;
    }

    .muted {
      color: #647084;
    }

    .center {
      padding: 30px;
      color: #647084;
      font-size: 20px;
      text-align: center;
    }

    .load-more {
      width: calc(100% - 36px);
      margin: 18px;
      min-height: 58px;
      border-color: #1d4ed8;
      color: #1d4ed8;
      font-size: 20px;
      font-weight: 900;
    }

    pre {
      white-space: pre-wrap;
      word-break: break-word;
    }

    @media (max-width: 860px) {
      .top {
        grid-template-columns: 1fr;
      }

      .layout {
        grid-template-columns: 1fr;
      }

      .list, .detail {
        max-height: none;
      }

      .detail-head {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>
<body>
  <header class="top">
    <input id="apiBase" class="api" value="http://localhost:8080" aria-label="Backend URL" />
    <div class="actions">
      <button id="login" class="btn primary">Google 로그인</button>
      <button id="me" class="btn">로그인 상태 확인</button>
      <button id="refreshClubs" class="btn">동아리 다시 불러오기</button>
      <button id="logout" class="btn danger">토큰 삭제</button>
    </div>
  </header>

  <section id="status" class="status-card">
    <strong>로그인 전</strong>
    <p>Google 로그인 버튼을 누르면 백엔드 OAuth 로그인으로 이동합니다.</p>
  </section>

  <main class="layout">
    <section id="list" class="list">
      <div class="center">로그인 후 동아리 목록을 불러옵니다.</div>
    </section>
    <section id="detail" class="detail">
      <div class="detail-card">
        <h2>테스트 순서</h2>
        <p>1. Google 로그인<br />2. 로그인 상태 확인<br />3. 왼쪽 동아리 목록 스크롤<br />4. 동아리 클릭 후 상세/모집 정보 확인</p>
      </div>
    </section>
  </main>

  <script>
    const state = {
      token: localStorage.getItem("dongneToken") || "",
      page: 0,
      size: 10,
      hasNext: true,
      loading: false,
      loadedCount: 0,
    };

    const $ = (id) => document.getElementById(id);
    const apiBaseInput = $("apiBase");
    const list = $("list");
    const detail = $("detail");
    const statusBox = $("status");

    const params = new URLSearchParams(window.location.search);
    const tokenFromUrl = params.get("token");
    const errorMessage = params.get("message");
    const hasLoginError = params.has("error") || window.location.pathname.startsWith("/login");

    if (tokenFromUrl) {
      state.token = tokenFromUrl;
      localStorage.setItem("dongneToken", tokenFromUrl);
      window.history.replaceState({}, document.title, "/");
      setStatus("로그인 성공", "백엔드 JWT를 받았습니다. 동아리 목록을 불러옵니다.");
    } else if (hasLoginError) {
      setStatus(
        "로그인 실패",
        errorMessage || "학교 계정(@inha.edu 또는 @inha.ac.kr)만 사용할 수 있습니다. 다른 계정으로 로그인했다면 토큰 삭제 후 다시 시도하세요.",
        true
      );
      window.history.replaceState({}, document.title, "/");
    }

    $("login").addEventListener("click", () => {
      setStatus("Google 로그인 이동 중", "백엔드 OAuth 시작 URL로 이동합니다.");
      window.location.href = apiBase() + "/oauth2/authorization/google";
    });

    $("me").addEventListener("click", async () => {
      setStatus("로그인 상태 확인 중", "/api/users/me API를 호출하고 있습니다.");
      await loadMe();
    });

    $("refreshClubs").addEventListener("click", resetAndLoadClubs);

    $("logout").addEventListener("click", () => {
      state.token = "";
      localStorage.removeItem("dongneToken");
      setStatus("토큰 삭제 완료", "다시 테스트하려면 Google 로그인을 눌러주세요.");
      list.innerHTML = '<div class="center">로그인 후 동아리 목록을 불러옵니다.</div>';
      detail.innerHTML = "";
    });

    list.addEventListener("scroll", () => {
      const nearBottom = list.scrollTop + list.clientHeight >= list.scrollHeight - 120;
      if (nearBottom) loadNextPage();
    });

    function apiBase() {
      return apiBaseInput.value.replace(/\/$/, "");
    }

    function setStatus(title, message, isError = false) {
      statusBox.className = "status-card" + (isError ? " error" : "");
      statusBox.innerHTML = '<strong>' + escapeHtml(title) + '</strong><p>' + escapeHtml(message) + '</p>';
    }

    function authHeaders() {
      if (!state.token) {
        throw new Error("토큰이 없습니다. 먼저 Google 로그인을 해주세요.");
      }

      return { Authorization: "Bearer " + state.token };
    }

    async function request(path) {
      const response = await fetch(apiBase() + path, { headers: authHeaders() });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(response.status + " " + response.statusText + (message ? "\n" + message : ""));
      }

      return response.json();
    }

    async function loadMe() {
      try {
        const me = await request("/api/users/me");
        setStatus("로그인됨", me.name + " <" + me.email + ">");
      } catch (error) {
        setStatus("로그인 상태 확인 실패", error.message, true);
        showError(error);
      }
    }

    async function resetAndLoadClubs() {
      state.page = 0;
      state.hasNext = true;
      state.loading = false;
      state.loadedCount = 0;
      list.innerHTML = "";
      setStatus("동아리 목록 로딩", "/api/clubs?page=0&size=" + state.size + " 호출 중입니다.");
      await loadNextPage();
    }

    async function loadNextPage() {
      if (state.loading || !state.hasNext) return;

      state.loading = true;
      appendCenter("불러오는 중...");

      try {
        const data = await request("/api/clubs?page=" + state.page + "&size=" + state.size);
        removeTemporaryNodes();

        data.content.forEach(renderClub);
        state.loadedCount += data.content.length;
        state.hasNext = data.hasNext;
        state.page = data.page + 1;

        if (!data.content.length && data.page === 0) {
          appendCenter("동아리 데이터가 없습니다.");
        } else if (!state.hasNext) {
          appendCenter("마지막입니다. 총 " + state.loadedCount + "개를 불러왔습니다.");
          setStatus("동아리 목록 로딩 완료", "총 " + state.loadedCount + "개를 불러왔습니다.");
        } else {
          appendLoadMore();
          setStatus("동아리 목록 로딩 중", "현재 " + state.loadedCount + "개를 불러왔습니다. 아래로 스크롤하거나 더 불러오기를 누르세요.");
          queueFillList();
        }
      } catch (error) {
        removeTemporaryNodes();
        appendCenter(error.message, true);
        setStatus("동아리 목록 조회 실패", error.message, true);
      } finally {
        state.loading = false;
      }
    }

    function queueFillList() {
      window.setTimeout(() => {
        const cannotScrollYet = list.scrollHeight <= list.clientHeight + 20;
        if (cannotScrollYet && state.hasNext && !state.loading) {
          loadNextPage();
        }
      }, 0);
    }

    function renderClub(club) {
      const button = document.createElement("button");
      button.className = "club";
      button.dataset.clubId = club.id;
      button.innerHTML =
        '<img src="' + escapeHtml(club.profileImg || "") + '" alt="">' +
        '<div>' +
          '<span class="badge">' + escapeHtml(club.category || "동아리") + '</span>' +
          '<strong>' + escapeHtml(club.name) + '</strong>' +
          '<p>' + escapeHtml(club.description || "") + '</p>' +
        '</div>';

      button.addEventListener("click", () => loadClubDetail(club.id));
      list.appendChild(button);
    }

    async function loadClubDetail(clubId) {
      document.querySelectorAll(".club").forEach((item) => {
        item.classList.toggle("active", item.dataset.clubId === String(clubId));
      });

      detail.innerHTML = '<div class="detail-card"><h2>상세 정보 로딩 중</h2><p>동아리 상세 API를 호출하고 있습니다.</p></div>';

      try {
        const club = await request("/api/clubs/" + clubId);
        detail.innerHTML =
          '<article class="detail-card">' +
            '<div class="detail-head">' +
              '<img src="' + escapeHtml(club.profileImg || "") + '" alt="">' +
              '<div>' +
                '<span class="badge">' + escapeHtml(club.category || "동아리") + '</span>' +
                '<h1>' + escapeHtml(club.name) + '</h1>' +
                '<p>' + escapeHtml(club.description || "") + '</p>' +
              '</div>' +
            '</div>' +
            renderRecruitments(club.recruitments || []) +
            '<details class="section" open>' +
              '<summary><strong>자세한 활동</strong></summary>' +
              '<p>' + escapeHtml(club.activityDescription || "등록된 활동 설명이 없습니다.") + '</p>' +
            '</details>' +
            '<section class="section">' +
              '<h2>연락처</h2>' +
              '<p>' + escapeHtml(club.contact || "등록된 연락처가 없습니다.") + '</p>' +
              '<p class="muted">' + escapeHtml(club.instagramUrl || "") + '</p>' +
            '</section>' +
          '</article>';
      } catch (error) {
        showError(error);
      }
    }

    function renderRecruitments(recruitments) {
      if (!recruitments.length) {
        return '<section class="section"><h2>모집 정보</h2><p class="muted">현재 활성화된 모집 공고가 없습니다.</p></section>';
      }

      return '<section class="section"><h2>모집 정보</h2>' + recruitments.map((item) => {
        const period = item.alwaysOpen
          ? "상시모집"
          : [item.startDate, item.endDate].filter(Boolean).join(" - ");

        return '<div class="recruitment">' +
          '<strong>' + escapeHtml(item.title) + '</strong>' +
          '<p>' + escapeHtml(item.summary || "") + '</p>' +
          '<div class="muted">' + escapeHtml(period || "모집 기간 미정") + '</div>' +
        '</div>';
      }).join("") + '</section>';
    }

    function appendLoadMore() {
      removeTemporaryNodes();

      const button = document.createElement("button");
      button.className = "btn load-more";
      button.dataset.temporary = "true";
      button.textContent = "더 불러오기";
      button.addEventListener("click", loadNextPage);
      list.appendChild(button);
    }

    function appendCenter(message, isError = false) {
      const node = document.createElement("div");
      node.className = "center" + (isError ? " error" : "");
      node.dataset.temporary = "true";
      node.textContent = message;
      list.appendChild(node);
    }

    function removeTemporaryNodes() {
      list.querySelectorAll('[data-temporary="true"]').forEach((node) => node.remove());
    }

    function showError(error) {
      detail.innerHTML = '<div class="detail-card error"><h2>오류</h2><pre>' + escapeHtml(error.message) + '</pre></div>';
    }

    function escapeHtml(value) {
      return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
    }

    if (state.token) {
      loadMe().finally(resetAndLoadClubs);
    }
  </script>
</body>
</html>`;

const server = http.createServer((request, response) => {
  response.writeHead(200, {
    "Content-Type": "text/html; charset=utf-8",
    "Cache-Control": "no-store",
  });
  response.end(html);
});

server.listen(PORT, () => {
  console.log(`OAuth club test page: http://localhost:${PORT}`);
});
