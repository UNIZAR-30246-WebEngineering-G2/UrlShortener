$(document).ready(
    function () {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' id='idLink' onclick='check(event);' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error: function(xhr, exception){
                        if( xhr.status === 409){
                            var json = JSON.parse(xhr.responseText);
                            $("#result").html(
                                "<div class='alert alert-warning lead'>Error: the shortened URL already" +
                                " exists, and therefore you can't be the owner. You can still use it though: " +
                                "<a target='_blank' id='idLink' onclick='check(event);' href='"
                                + json.uri
                                + "'>"
                                + json.uri
                                + "</a></div>");
                        } else if( xhr.status === 503){
                            $("#result").html(
                                "<div class='alert alert-danger lead'>Error: the url provided is unreachable" +
                                "</div>");
                        }
                        else
                            $("#result").html(
                                "<div class='alert alert-danger lead'>Error: oops, something went wrong!</div>");
                    }
                });
            });
    });