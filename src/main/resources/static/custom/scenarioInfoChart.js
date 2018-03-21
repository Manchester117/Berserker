var sock = null;
var stompClient = null;
var samplerInfo = null;

$(document).ready(
    function samplerSocket() {
        sock = new SockJS('/socket');
        stompClient = Stomp.over(sock);
        stompClient.connect({}, function () {
            console.log('notice socket connected!');
            stompClient.subscribe('/topic/notice', function (data) {
                // 拿到数据后将字符串转成JSON对象
                samplerInfo = $.parseJSON(data.body);
                console.log(data);
            });
        });
    }
);

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

$('#mean_time_chart').highcharts({
    chart: {
        type: 'spline',
        animation: Highcharts.svg, // don't animate in old IE
        marginRight: 10,
        events: {
            load: function () {
                var seriesMeanTime = this.series[0];

                setInterval(function () {
                    if (samplerInfo) {
                        var currentTime = (new Date()).getTime();
                        var meanTime = samplerInfo["meanTime"];
                        seriesMeanTime.addPoint([currentTime, meanTime], true, false);   // 第三个参数,设置为false,说明将以前的数据积累下来
                        samplerInfo = null;
                    }
                }, 250);
            }
        }
    },
    series: [
        {
            name: '平均响应时间',
            data: []
        }
    ],
    title: {
        text: '平均响应时间-实时'
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 10
    },
    yAxis: {
        title: {
            text: '毫秒'
        },
        plotLines: [{
            value: 0,
            width: 1,
            color: 'red'
        }]
    },
    plotOptions: {
        series: {
            marker: {
                enabled: false          // 不显示数据点
            }
        }
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
    credits: {
        enabled: false     // 不显示LOGO
    }
});

// $('#request_rate_chart').highcharts({
//     chart: {
//         type: 'spline',
//         animation: Highcharts.svg, // don't animate in old IE
//         marginRight: 10,
//         events: {
//             load: function () {
//                 var seriesMeanTime = this.series[0];
//
//                 setInterval(function () {
//                     if (samplerInfo) {
//                         var currentTime = (new Date()).getTime();
//                         var threadCount = samplerInfo["requestRate"];
//                         seriesMeanTime.addPoint([currentTime, threadCount], true, false);   // 第三个参数,设置为false,说明将以前的数据积累下来
//                         samplerInfo = null;
//                     }
//                 }, 10);
//             }
//         }
//     },
//     series: [
//         {
//             name: '每秒请求处理量',
//             data: []
//         }
//     ],
//     title: {
//         text: '每秒请求处理量-实时'
//     },
//     xAxis: {
//         type: 'datetime',
//         tickPixelInterval: 150
//     },
//     yAxis: {
//         title: {
//             text: '个'
//         },
//         plotLines: [{
//             value: 0,
//             width: 1,
//             color: 'green'
//         }]
//     },
//     plotOptions: {
//         series: {
//             marker: {
//                 enabled: false          // 不显示数据点
//             }
//         }
//     },
//     tooltip: {
//         formatter: function () {
//             return '<b>' + this.series.name + '</b><br/>' +
//                 Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
//                 Highcharts.numberFormat(this.y, 2);
//         }
//     },
//     legend: {
//         enabled: false
//     },
//     exporting: {
//         enabled: false
//     },
//     credits: {
//         enabled: false     // 不显示LOGO
//     }
// });
