$(document).ready(
    function () {
        $("#updateLongitude").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/blockedLongitude",
                    data : $(this).serialize(),
                    success : function(msg) {
                        $("#resultLongitude").html(
                            "<div class='alert alert-success lead'>Longitude updated!</div>");
                    },
                    error: function(xhr, exception){
                        $("#resultLongitude").html(
                            "<div class='alert alert-danger lead'>Oops, something went wrong!</div>");

                    }
                });
            });
    });