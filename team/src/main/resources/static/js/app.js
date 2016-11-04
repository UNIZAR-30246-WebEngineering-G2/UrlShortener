$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error: function(xhr, exception){
                        if( xhr.status === 409){
                            var json = JSON.parse(xhr.responseText);
                            $("#result").html(
                                "<div class='alert alert-danger lead'>Error: the shortened URL already" +
                                " exists, and therefore you can't be the owner. You can still use it though: " +
                                "<a target='_blank' href='"
                                + json.uri
                                + "'>"
                                + json.uri
                                + "</a></div>");
                        }
                        else
                            $("#result").html(
                                "<div class='alert alert-danger lead'>Error: oops, something went wrong!</div>");
                    }
                });
            });
    });