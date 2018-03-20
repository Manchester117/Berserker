var samplerInfo = null;

$(document).ready(
    function samplerSocket() {
        var sock = new SockJS('/socket');
        var stompClient = Stomp.over(sock);
        stompClient.connect({}, function () {
            console.log('notice socket connected!');
            stompClient.subscribe('/topic/notice', function (data) {
                // 拿到数据后将字符串转成JSON对象
                samplerInfo = $.parseJSON(data.body);
                // $('#sampler_charts').append("<label>" + samplerInfo["meanTime"] + "</label><br/>");
                // if (samplerInfo["engineIsActive"] === false) {
                //     // 当引擎不在运行则断开Socket连接
                //     stompClient.disconnect();
                // }
            });
        });
    }
);

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

function activeLastPointToolTip(chart) {
    var points = chart.series[0].points;
    chart.tooltip.refresh(points[points.length - 1]);
}

$('#mean_time_chart').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg, // don't animate in old IE
        marginRight: 10,
        events: {
            load: function () {
                // set up the updating of the chart each second
                var series = this.series[0],
                    chart = this;
                setInterval(function () {
                    var x = (new Date()).getTime(),         // current time
                        y = samplerInfo["meanTime"];
                    console.log(samplerInfo);
                    series.addPoint([x, y], true, false);   // 第三个参数,设置为false,说明将以前的数据积累下来
                    activeLastPointToolTip(chart)
                }, 500);
            }
        }
    },
    title: {
        text: '平均响应时间-实时显示'
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 150
    },
    yAxis: {
        title: {
            text: '毫秒'
        },
        plotLines: [{
            value: 0,
            width: 1,
            color: '#808080'
        }]
    },
    tooltip: {
        formatter: function () {
            return '<b>' + this.series.name + '</b><br/>' +
                Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                Highcharts.numberFormat(this.y, 2);
        }
    },
    legend: {
        enabled: false
    },
    exporting: {
        enabled: false
    },
    series: [{
        name: '平均响应时间',
        data: (function () {
            // 这里是设定初始时间和基础数据
            // generate an array of random data
            var data = [],
                time = (new Date()).getTime(),
                i;
            // 为什么i的初值要-19... 不明白...
            for (i = -1; i <= 0; i += 1) {
                data.push({
                    x: time + i * 1000,
                    y: 0
                });
            }
            return data;
        }())
    }]
}, function(c) {
    activeLastPointToolTip(c)
});
