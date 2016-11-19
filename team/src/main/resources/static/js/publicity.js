function startTimer(duration, display) {
    var timer = duration, seconds;
    setInterval(function () {
        seconds = parseInt(timer % 60, 10);

        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.text(seconds);

        if (--timer < 0) {
            $.ajax({
                type : "GET",
                url : "/obtainPublicityLink",
                success : function(msg) {
                    window.location = msg;
                }
            });
        }
    }, 1000);
}

jQuery(function ($) {
    var tenseconds = $('#time').text(),
        display = $('#time');
    startTimer(tenseconds, display);
});