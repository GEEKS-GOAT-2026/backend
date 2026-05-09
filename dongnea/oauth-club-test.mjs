import http from "node:http";

const PORT = 3000;

const html = String.raw`<!doctype html>
<html lang="ko">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Dongne OAuth Club Test</title>
  <style>
    :root {
      color-scheme: light;
      font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      background: #f6f7f9;
      color: #17191c;
    }

    * {
      box-sizing: border-box;
    }

    body {
      margin: 0;
    }

    header {
      position: sticky;
      top: 0;
      z-index: 10;
      display: flex;
      gap: 12px;
      align-items: center;
      justify-content: space-between;
      padding: 14px 18px;
      border-bottom: 1px solid #dfe3e8;
      background: rgba(255, 255, 255, 0.94);
      backdrop-filter: blur(8px);
    }

    main {
      display: grid;
      grid-template-columns: minmax(0, 420px) minmax(0, 1fr);
      min-height: calc(100vh - 65px);
    }

    button, input {
      height: 38px;
      border: 1px solid #c8cfd8;
      border-radius: 6px;
      background: #fff;
      color: #17191c;
      font: inherit;
    }

    button {
      cursor: pointer;
      padding: 0 12px;
    }

    button.primary {
      border-color: #1d4ed8;
      background: #1d4ed8;
      color: #fff;
      font-weight: 700;
    }

    input {
      width: 280px;
      padding: 0 10px;
    }

    .bar-left, .bar-right {
      display: flex;
      gap: 8px;
      align-items: center;
      min-width: 0;
    }

    .user {
      max-width: 380px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: #4b5563;
      font-size: 14px;
    }

    .list {
      border-right: 1px solid #dfe3e8;
      background: #fff;
      overflow-y: auto;
      max-height: calc(100vh - 65px);
    }

    .club {
      width: 100%;
      height: auto;
      display: grid;
      grid-template-columns: 64px minmax(0, 1fr);
      gap: 12px;
      padding: 14px;
      border: 0;
      border-bottom: 1px solid #edf0f3;
      border-radius: 0;
      text-align: left;
    }

    .club:hover, .club.active {
      background: #f1f5ff;
    }

    .club img {
      width: 64px;
      height: 64px;
      border-radius: 8px;
      object-fit: cover;
      background: #edf0f3;
    }

    .club strong {
      display: block;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 15px;
    }

    .club p {
      margin: 0;
      color: #4b5563;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      font-size: 13px;
      line-height: 1.45;
    }

    .badge {
      display: inline-flex;
      margin-bottom: 6px;
      padding: 2px 7px;
      border-radius: 999px;
      background: #e8eefc;
      color: #1d4ed8;
      font-size: 12px;
    }

    .detail {
      padding: 28px;
      overflow-y: auto;
      max-height: calc(100vh - 65px);
    }

    .detail img {
      width: 112px;
      height: 112px;
      border-radius: 12px;
      object-fit: cover;
      background: #edf0f3;
    }

    .detail h1 {
      margin: 14px 0 8px;
      font-size: 28px;
      letter-spacing: 0;
    }

    .detail p {
      line-height: 1.65;
      color: #333942;
    }

    .panel {
      margin-top: 18px;
      padding: 16px;
      border: 1px solid #dfe3e8;
      border-radius: 8px;
      background: #fff;
    }

    .panel h2 {
      margin: 0 0 10px;
      font-size: 16px;
    }

    .recruitment {
      padding: 12px 0;
      border-top: 1px solid #edf0f3;
    }

    .recruitment:first-of-type {
      border-top: 0;
      padding-top: 0;
    }

    .muted {
      color: #6b7280;
      font-size: 13px;
    }

    .status {
      padding: 20px;
      color: #6b7280;
      text-align: center;
    }

    .error {
      color: #b42318;
    }

    @media (max-width: 760px) {
      header {
        align-items: stretch;
        flex-direction: column;
      }

      .bar-left, .bar-right {
        flex-wrap: wrap;
      }

      input {
        width: 100%;
      }

      main {
        grid-template-columns: 1fr;
      }

      .list, .detail {
        max-height: none;
      }
    }
  </style>
</head>
<body>
  <header>
    <div class="bar-left">
      <input id="apiBase" value="http://localhost:8080" aria-label="Backend URL" />
      <button id="login" class="primary">Google 로그인</button>
      <button id="me">내 정보</button>
      <button id="logout">토큰 삭제</button>
    </div>
    <div class="bar-right">
      <span id="user" class="user">로그인 전</span>
    </div>
  </header>

  <main>
    <section id="list" class="list">
      <div class="status">로그인 후 동아리 목록을 불러옵니다.</div>
    </section>
    <section id="detail" class="detail">
      <div class="panel">
        <h2>테스트 흐름</h2>
        <p>Google 로그인 후 왼쪽 목록을 스크롤하고, 동아리를 클릭하면 상세 정보와 모집 정보를 확인합니다.</p>
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
      selectedId: null,
    };

    const $ = (id) => document.getElementById(id);
    const apiBaseInput = $("apiBase");
    const list = $("list");
    const detail = $("detail");
    const user = $("user");

    const params = new URLSearchParams(window.location.search);
    const tokenFromUrl = params.get("token");

    if (tokenFromUrl) {
      state.token = tokenFromUrl;
      localStorage.setItem("dongneToken", tokenFromUrl);
      window.history.replaceState({}, document.title, "/");
    }

    $("login").addEventListener("click", () => {
      window.location.href = apiBase() + "/oauth2/authorization/google";
    });

    $("me").addEventListener("click", loadMe);

    $("logout").addEventListener("click", () => {
      state.token = "";
      localStorage.removeItem("dongneToken");
      user.textContent = "로그인 전";
      list.innerHTML = '<div class="status">토큰을 삭제했습니다.</div>';
      detail.innerHTML = "";
    });

    list.addEventListener("scroll", () => {
      const nearBottom = list.scrollTop + list.clientHeight >= list.scrollHeight - 80;
      if (nearBottom) loadNextPage();
    });

    function apiBase() {
      return apiBaseInput.value.replace(/\/$/, "");
    }

    function authHeaders() {
      if (!state.token) {
        throw new Error("토큰이 없습니다. 먼저 Google 로그인을 해주세요.");
      }

      return {
        Authorization: "Bearer " + state.token,
      };
    }

    async function request(path) {
      const response = await fetch(apiBase() + path, {
        headers: authHeaders(),
      });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(response.status + " " + response.statusText + (message ? "\n" + message : ""));
      }

      return response.json();
    }

    async function loadMe() {
      try {
        const me = await request("/api/users/me");
        user.textContent = me.name + " <" + me.email + ">";
      } catch (error) {
        user.textContent = "내 정보 조회 실패";
        showError(error);
      }
    }

    async function resetAndLoadClubs() {
      state.page = 0;
      state.hasNext = true;
      state.loading = false;
      list.innerHTML = "";
      await loadNextPage();
    }

    async function loadNextPage() {
      if (state.loading || !state.hasNext) return;

      state.loading = true;
      appendStatus("불러오는 중...");

      try {
        const data = await request("/api/clubs?page=" + state.page + "&size=" + state.size);
        removeStatus();

        data.content.forEach(renderClub);
        state.hasNext = data.hasNext;
        state.page = data.page + 1;

        if (!data.content.length && data.page === 0) {
          appendStatus("동아리 데이터가 없습니다.");
        } else if (!state.hasNext) {
          appendStatus("마지막 동아리입니다.");
        }
      } catch (error) {
        removeStatus();
        appendStatus(error.message, true);
      } finally {
        state.loading = false;
      }
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
      state.selectedId = clubId;
      document.querySelectorAll(".club").forEach((item) => {
        item.classList.toggle("active", item.dataset.clubId === String(clubId));
      });

      detail.innerHTML = '<div class="status">상세 정보를 불러오는 중...</div>';

      try {
        const club = await request("/api/clubs/" + clubId);
        detail.innerHTML =
          '<img src="' + escapeHtml(club.profileImg || "") + '" alt="">' +
          '<h1>' + escapeHtml(club.name) + '</h1>' +
          '<span class="badge">' + escapeHtml(club.category || "동아리") + '</span>' +
          '<p>' + escapeHtml(club.description || "") + '</p>' +
          renderRecruitments(club.recruitments || []) +
          '<details class="panel" open>' +
            '<summary><strong>자세한 활동</strong></summary>' +
            '<p>' + escapeHtml(club.activityDescription || "등록된 활동 설명이 없습니다.") + '</p>' +
          '</details>' +
          '<div class="panel">' +
            '<h2>연락처</h2>' +
            '<p>' + escapeHtml(club.contact || "등록된 연락처가 없습니다.") + '</p>' +
            '<p class="muted">' + escapeHtml(club.instagramUrl || "") + '</p>' +
          '</div>';
      } catch (error) {
        showError(error);
      }
    }

    function renderRecruitments(recruitments) {
      if (!recruitments.length) {
        return '<div class="panel"><h2>모집 정보</h2><p class="muted">현재 활성화된 모집 공고가 없습니다.</p></div>';
      }

      return '<div class="panel"><h2>모집 정보</h2>' + recruitments.map((item) => {
        const period = item.alwaysOpen
          ? "상시모집"
          : [item.startDate, item.endDate].filter(Boolean).join(" - ");

        return '<div class="recruitment">' +
          '<strong>' + escapeHtml(item.title) + '</strong>' +
          '<p>' + escapeHtml(item.summary || "") + '</p>' +
          '<div class="muted">' + escapeHtml(period || "모집 기간 미정") + '</div>' +
        '</div>';
      }).join("") + '</div>';
    }

    function appendStatus(message, isError = false) {
      const node = document.createElement("div");
      node.className = "status" + (isError ? " error" : "");
      node.dataset.status = "true";
      node.textContent = message;
      list.appendChild(node);
    }

    function removeStatus() {
      list.querySelectorAll('[data-status="true"]').forEach((node) => node.remove());
    }

    function showError(error) {
      detail.innerHTML = '<div class="panel error"><h2>오류</h2><pre>' + escapeHtml(error.message) + '</pre></div>';
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
