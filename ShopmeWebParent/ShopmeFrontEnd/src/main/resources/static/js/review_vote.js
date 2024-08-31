$(document).ready(function() {
    $(".linkVoteReview").on("click", function(e) {
        e.preventDefault();
        voteReview($(this));
    });
});

function voteReview(currentLink) {
   let requestURL = currentLink.attr("href");

    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(voteResult) {
        console.log(voteResult);

        if (voteResult.successful) {
            $("#modalDialog").on("hide.bs.modal", function(e) {
                updateVoteCountAndIcons(currentLink, voteResult);
            });
        }

        showModalDialog("Vote Review", voteResult.message);

    }).fail(function() {
        showErrorModal("Error voting review.");
    });
}

function updateVoteCountAndIcons(currentLink, voteResult) {
    let reviewId = currentLink.attr("reviewId");
    let voteUpLink = $("#linkVoteUp-" + reviewId);
    let voteDownLink = $("#linkVoteDown-" + reviewId);

    $("#voteCount-" + reviewId).text(voteResult.voteCount + " Votes");

    let message = voteResult.message;

    if (message.includes("successfully voted up")) {
        highlightVoteUpIcon(currentLink, voteDownLink);
    } else if (message.includes("successfully voted down")) {
        highlightVoteDownIcon(currentLink, voteUpLink);
    } else if (message.includes("unvoted down")) {
        unhighlightVoteDownIcon(voteDownLink);
    } else if (message.includes("unvoted up")) {
        unhighlightVoteDownIcon(voteUpLink);
    }
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {
    voteUpLink.children("i").removeClass("far").addClass("fas");
    voteUpLink.attr("title", "Undo vote up this review");
    voteDownLink.children("i").removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
    voteDownLink.children("i").removeClass("far").addClass("fas");
    voteDownLink.attr("title", "Undo vote down this review");
    voteUpLink.children("i").removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink) {
    voteDownLink.attr("title", "Vote down this review");
    voteDownLink.children("i").removeClass("fas").addClass("far");
}

function unhighlightVoteUpIcon(voteUpLink) {
    voteUpLink.attr("title", "Vote up this review");
    voteUpLink.children("i").removeClass("fas").addClass("far");
}