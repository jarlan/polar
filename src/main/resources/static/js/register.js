(function () {
    var STORAGE_KEY = 'danceParticipantId';
    var myIdInput = document.getElementById('myId');

    if (!myIdInput) return;

    var storedId = localStorage.getItem(STORAGE_KEY);
    if (storedId) {
        myIdInput.value = storedId;
    }

    var form = myIdInput.closest('form');
    if (form) {
        form.addEventListener('submit', function () {
            if (myIdInput.value) {
                localStorage.setItem(STORAGE_KEY, myIdInput.value);
            }
        });
    }
})();
