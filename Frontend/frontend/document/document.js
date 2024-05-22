document.addEventListener("DOMContentLoaded", function() {
    var documentBody = document.getElementById("document-body");
    var wordCount = document.getElementById("word-count");

    // Update word count every time the user types something
    documentBody.addEventListener("input", function() {
        var text = documentBody.innerText;
        var words = text.trim().split(/\s+/).length;
        wordCount.innerText = "Word count: " + words;
    });

    // Reset word count when user clears the document area
    documentBody.addEventListener("keydown", function(event) {
        if (event.keyCode === 8 || event.keyCode === 46) { // Backspace or Delete key
            if (documentBody.innerText === "") {
                wordCount.innerText = "Word count: 0";
            }
        }
    });

    // Disable context menu
    documentBody.oncontextmenu = function() {
        return false;
    };

    // Disable keyboard shortcuts for copying and pasting
    documentBody.onkeydown = function(event) {
        if (event.ctrlKey || event.metaKey) {
            switch (event.keyCode) {
                case 67: // Ctrl+C
                case 86: // Ctrl+V
                case 65: // Ctrl+A
                    event.preventDefault();
                    return false;
            }
        }
    };

    // Disable paste event
    documentBody.onpaste = function(event) {
        event.preventDefault();
        return false;
    };
});
