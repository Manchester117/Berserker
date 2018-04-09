var $stopScenarioRun = $("#stopScenarioRun");
$stopScenarioRun.click(
    function() {
        $.ajax({
            url: "/scenarioStopRun",
            type: "get",
            success: function(result) {
                    bootbox.alert({
                        title: '提示',
                        message: result['message'],
                        callback: function() {
                            $(window).attr('location', "/");
                        }
                    });
            },
            error: function(result) {}
        });
    }
);
