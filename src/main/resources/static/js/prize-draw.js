const drawForm = document.getElementById('drawForm');
const startDrawButton = document.getElementById('startDrawButton');
const thresholdInput = document.getElementById('thresholdInput');
const candidateGrid = document.getElementById('candidateGrid');
const drawMessage = document.getElementById('drawMessage');
const winnerReveal = document.getElementById('winnerReveal');
const winnerName = document.getElementById('winnerName');
const winnerPartners = document.getElementById('winnerPartners');

drawForm.addEventListener('submit', async function (event) {
    event.preventDefault();

    const threshold = thresholdInput.value;
    startDrawButton.disabled = true;
    startDrawButton.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Trekker...';

    drawMessage.innerHTML = 'Henter kandidater og klargjør scenen...';
    winnerReveal.classList.add('d-none');

    try {
        const response = await fetch('/admin/prize-draw/animate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ threshold })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }

        const result = await response.json();
        renderCandidates(result.candidates);
        await runShowDraw(result.winner);
        revealWinner(result.winner);
    } catch (error) {
        drawMessage.innerHTML = `<span class="text-danger"><i class="bi bi-exclamation-triangle me-2"></i>${escapeHtml(error.message)}</span>`;
    } finally {
        startDrawButton.disabled = false;
        startDrawButton.innerHTML = '<i class="bi bi-lightning-charge-fill me-2"></i>Start dramatisk trekking';
    }
});

function renderCandidates(candidates) {
    candidateGrid.innerHTML = '';

    candidates.forEach(candidate => {
        const card = document.createElement('article');
        card.className = 'candidate-card';
        card.dataset.participantId = candidate.participantId;

        card.innerHTML = `
            <div class="candidate-icon">
                <i class="bi bi-person-arms-up"></i>
            </div>
            <div class="candidate-name">${escapeHtml(candidate.name)}</div>
            <div class="candidate-score">
                <i class="bi bi-people-fill me-1"></i>
                ${candidate.uniquePartners}
                partnere
            </div>
        `;

        candidateGrid.appendChild(card);
    });

    drawMessage.innerHTML = `Trekker blant <strong class="text-dance">${candidates.length}</strong> kandidater...`;
}

async function runShowDraw(winner) {
    const cards = Array.from(document.querySelectorAll('.candidate-card'));

    if (cards.length === 0) {
        throw new Error('Ingen kandidater å trekke blant.');
    }

    document.body.classList.add('draw-running');

    const suspenseSteps = 42;
    let delay = 65;

    for (let i = 0; i < suspenseSteps; i++) {
        cards.forEach(card => card.classList.remove('candidate-active'));

        const randomCard = cards[Math.floor(Math.random() * cards.length)];
        randomCard.classList.add('candidate-active');

        if (i > suspenseSteps * 0.55) {
            delay += 22;
        }

        await sleep(delay);
    }

    cards.forEach(card => card.classList.remove('candidate-active'));

    const winnerCard = cards.find(card => Number(card.dataset.participantId) === Number(winner.participantId));

    if (winnerCard) {
        winnerCard.classList.add('candidate-winner');
        winnerCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    document.body.classList.remove('draw-running');
    createConfetti();
    await sleep(900);
}

function revealWinner(winner) {
    winnerName.textContent = winner.name;
    winnerPartners.textContent = `${winner.uniquePartners} unike partnere`;
    winnerReveal.classList.remove('d-none');
    drawMessage.innerHTML = '<span class="text-warning fw-bold"><i class="bi bi-trophy-fill me-2"></i>Vi har en vinner!</span>';
}

function createConfetti() {
    for (let i = 0; i < 90; i++) {
        const confetti = document.createElement('div');
        confetti.className = 'draw-confetti';
        confetti.style.left = `${Math.random() * 100}%`;
        confetti.style.animationDelay = `${Math.random() * 0.8}s`;
        confetti.style.backgroundColor = randomConfettiColor();
        document.body.appendChild(confetti);

        setTimeout(() => confetti.remove(), 3500);
    }
}

function randomConfettiColor() {
    const colors = ['#e91e8c', '#9c27b0', '#ffc107', '#00e5ff', '#ffffff'];
    return colors[Math.floor(Math.random() * colors.length)];
}

function sleep(milliseconds) {
    return new Promise(resolve => setTimeout(resolve, milliseconds));
}

function escapeHtml(value) {
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}