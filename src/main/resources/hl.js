/*
* This file is used in sample.AlternativeXmlWevViewHelper
* */
function update(text) {
    $("pre").text(text)
}
function highlight(text) {
    var color = "yellow";
    $("#dummy").css('color', color);

    unhighlight(document.body, color);
    searchAndHighlight(text, color);
}

function searchAndHighlight(text, colour) {
    if (window.find) {
        if (window.getSelection) {
            document.designMode = "on";
            var sel = window.getSelection();
            sel.collapse(document.body, 0);

            while (window.find(text)) {
                document.execCommand("HiliteColor", false, colour);
                sel.collapseToEnd();
            }
            document.designMode = "off";
        }
    }
}

function unhighlight() {
    $("span").filter(function () {
        return $(this).css('background-color') == $('#dummy').css('color');
    }).contents().unwrap();
}