function check(event){
    event.preventDefault();
    var href = $("#idLink").attr("href");
    var hrefBySlash = href.split("/");
    var hash = hrefBySlash[3];
    var importantStuff = window.open('', '_blank');
    $.get("/requestStatus", {link: hash}, function(msg){
        if(msg != 'ok'){
            importantStuff.close();
            document.getElementById("idRequestsError").style.display = 'block';
        } else importantStuff.location.href = href;
    });
    return false;
}

function closeRequestError(){
    document.getElementById("idRequestsError").style.display = 'none';
}