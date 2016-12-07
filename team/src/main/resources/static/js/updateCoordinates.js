$(document).ready(
    function () {
        $("#updateCoordinates").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/blockedCoordinates",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#resultUpdate").html(
                            "<div class='alert alert-success lead'>Coordinates updated!</div>");
                    },
                    error: function(xhr, exception){
                        $("#resultUpdate").html(
                            "<div class='alert alert-danger lead'>Oops, something went wrong!</div>");

                    }
                });
            });
    });