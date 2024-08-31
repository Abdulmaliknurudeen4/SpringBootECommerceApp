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
    let voteUpLink = $("#linkVoteUp-" + reviewId+" i");
    let voteDownLink = $("#linkVoteDown-" + reviewId+" i");

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

   /* let reviewId = currentLink.attr("reviewId");
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
    }*/
}

function highlightVoteUpIcon(voteUpLink, voteDownLink) {

     voteUpLink.removeClass("far").addClass("fas");
    voteUpLink.attr("title", "Undo vote up this review");
    voteDownLink.removeClass("fas").addClass("far");

   /* voteUpLink.removeClass("far").addClass("fa");
    voteUpLink.attr("title", "Undo vote up this review");
    voteDownLink.removeClass("fas").addClass("far");*/
}

function highlightVoteDownIcon(voteDownLink, voteUpLink) {
    voteDownLink.removeClass("far").addClass("fas");
     voteDownLink.attr("title", "Undo vote down this review");
     voteUpLink.removeClass("fas").addClass("far");

    /*voteDownLink.removeClass("far").addClass("fa");
    voteDownLink.attr("title", "Undo vote down this review");
    voteUpLink.removeClass("fas").addClass("far");*/
}

function unhighlightVoteDownIcon(voteDownLink) {
     voteDownLink.attr("title", "Vote down this review");
     voteDownLink.removeClass("fas").addClass("far");

   /* voteDownLink.attr("title", "Vote down this review");
    voteDownLink.removeClass("fa").addClass("far");*/
}

function unhighlightVoteUpIcon(voteUpLink) {
    voteUpLink.attr("title", "Vote up this review");
   voteUpLink.removeClass("fas").addClass("far");

    /*voteUpLink.attr("title", "Vote up this review");
    voteUpLink.removeClass("fas").addClass("far");*/
}
