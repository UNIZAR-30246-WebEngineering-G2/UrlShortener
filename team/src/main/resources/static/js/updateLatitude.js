$(document).ready(
    function () {
        $("#updateLatitude").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/blockedLatitude",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#resultLatitude").html(
                            "<div class='alert alert-success lead'>Latitude updated!</div>");
                    },
                    error: function(xhr, exception){
                        $("#resultLatitude").html(
                            "<div class='alert alert-danger lead'>Oops, something went wrong!</div>");

                    }
                });
            });
    });