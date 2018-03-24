var stompClient = null;
var samplerInfo = null;

/**
 * @description WebSocket建立连接,获取实时数据
 */
function sampleResultSocket(series, dataType) {
    var sock = new SockJS('/socket');
    stompClient = Stomp.over(sock);
    stompClient.connect({}, function (frame) {
        console.log('connected ' + frame);
        stompClient.subscribe('/sampleResult/data', function (data) {
            // 拿到数据后将字符串转成JSON对象
            samplerInfo = $.parseJSON(data.body);
            if (samplerInfo) {
                var timeStamp = samplerInfo["timeStamp"];
                var dataSampler = samplerInfo[dataType];
                series.addPoint([timeStamp, dataSampler], true, false);   // 第三个参数,设置为false,说明将以前的数据积累下来
                samplerInfo = null;
            }
        });
    });
}

Highcharts.setOptions({
    global: {
        useUTC: false
    }
});

/**
 * @description 动态显示趋势图
 */
$('#mean_time_chart').highcharts({
    series: [{
        name: '平均响应时间',
        data: []
    }],
    chart: {
        type: 'spline',
        animation: Highcharts.svg, // don't animate in old IE
        marginRight: 10,
        events: {
            load: function () {
                var series = this.series[0];
                sampleResultSocket(series, "meanTime");
            }
        }
    },
    title: {
        text: '平均响应时间'
    },
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 10,
        rotation: 10
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
//     series: [{
//         name: '每秒请求处理量',
//         data: []
//     }],
//     chart: {
//         type: 'spline',
//         animation: Highcharts.svg, // don't animate in old IE
//         marginRight: 10,
//         events: {
//             load: function () {
//                 var series = this.series[0];
//                 samplerSocket(series, "requestRate");
//             }
//         }
//     },
//     title: {
//         text: '每秒请求处理量'
//     },
//     xAxis: {
//         type: 'datetime',
//         tickPixelInterval: 5,
//         rotation: 30
//     },
//     yAxis: {
//         title: {
//             text: '个'
//         },
//         plotLines: [{
//             value: 0,
//             width: 1,
//             color: '#808080'
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
