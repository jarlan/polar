(function () {
    var statusEl = document.getElementById('connection-status');
    var tbodyEl = document.getElementById('leaderboard-body');

    function connect() {
        var socket = new SockJS('/ws');
        var stompClient = Stomp.over(socket);
        stompClient.debug = null;

        stompClient.connect({}, function (frame) {
            statusEl.textContent = '●';
            statusEl.className = 'ms-2 connected';

            stompClient.subscribe('/topic/leaderboard', function (message) {
                var leaderboard = JSON.parse(message.body);
                updateTable(leaderboard);
            });
        }, function (error) {
            statusEl.textContent = '●';
            statusEl.className = 'ms-2 disconnected';
            setTimeout(connect, 5000);
        });
    }

    function getMedal(rank) {
        if (rank === 1) return '🥇';
        if (rank === 2) return '🥈';
        if (rank === 3) return '🥉';
        return rank;
    }

    function getRankClass(rank) {
        if (rank === 1) return 'rank-1';
        if (rank === 2) return 'rank-2';
        if (rank === 3) return 'rank-3';
        return '';
    }

    function updateTable(leaderboard) {
        if (!tbodyEl) return;
        tbodyEl.innerHTML = '';

        if (leaderboard.length === 0) {
            tbodyEl.innerHTML = '<tr><td colspan="3" class="text-center text-muted py-5 fs-4">No dances yet. Time to hit the floor! 💃</td></tr>';
            return;
        }

        leaderboard.forEach(function (entry) {
            var tr = document.createElement('tr');
            tr.id = 'row-' + entry.participantId;
            tr.className = getRankClass(entry.rank) + ' animate-in';
            tr.innerHTML =
                '<td class="fs-3 fw-bold">' + getMedal(entry.rank) + '</td>' +
                '<td class="fs-3 fw-bold">' + escapeHtml(entry.name) + '</td>' +
                '<td class="fs-3 fw-bold text-end text-dance">' + entry.uniquePartners + '</td>';
            tbodyEl.appendChild(tr);
        });
    }

    function escapeHtml(text) {
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(text));
        return div.innerHTML;
    }

    connect();
})();
