var stompClient = null;
var samplerInfo = null;
var samplerFilter = {};

/**
 * @description WebSocket建立连接,获取实时数据
 */
// function sampleResultSocket(chart, dataType) {
//     var sock = new SockJS('/socket');
//     stompClient = Stomp.over(sock);
//     stompClient.connect({}, function (frame) {
//         // console.log('connected ' + frame);
//         stompClient.subscribe('/sampleResult/data', function (data) {
//             // 拿到数据后将字符串转成JSON对象
//             samplerInfo = $.parseJSON(data.body);
//             if (samplerInfo) {
//                 var timeStamp = samplerInfo["timeStamp"];
//                 var samplerLabel = samplerInfo["samplerLabel"];
//                 var samplerData = samplerInfo[dataType];
//
//                 if (samplerFilter[samplerLabel] === undefined) {
//                     var samplerDataList = [];
//                     samplerDataList.push({
//                         x: timeStamp,
//                         y: samplerData
//                     });
//                     // 注意!不能在data中使用生成数组的方法
//                     samplerFilter[samplerLabel] = chart.addSeries({
//                         name: samplerLabel,
//                         data: samplerDataList
//                     });
//                 } else {
//                     samplerFilter[samplerLabel].addPoint([timeStamp, samplerData], true, false, true);   // 第三个参数,设置为false,说明将以前的数据积累
//                 }
//                 samplerInfo = null;
//             }
//         });
//     });
// }

// /**
//  * @description HighCharts的全局时区设置
//  */
// Highcharts.setOptions({
//     global: {
//         useUTC: false
//     }
// });
//
// /**
//  * @description 动态显示趋势图
//  */
// var chartId = $("input[name='chartId']").val();
// $(chartId).highcharts({
//     series: [],
//     chart: {
//         type: 'spline',
//         animation: Highcharts.svg, // don't animate in old IE
//         marginRight: 10,
//         events: {
//             load: function () {
//                 sampleResultSocket(this, $("input[name='dataType']").val());
//             }
//         }
//     },
//     title: {
//         text: $("input[name='chartTitle']").val()
//     },
//     xAxis: {
//         type: 'datetime',
//         tickPixelInterval: 25,
//         rotation: 25
//     },
//     yAxis: {
//         title: {
//             text: $("input[name='unit']").val(),
//             style: {
//                 fontWeight: 'bold'
//             }
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
//             },
//             lineWidth: 1.5              // 线宽
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
//         enabled: false                  // 不显示LOGO
//     }
// });

function sampleResultSocket(chart, dataType) {
    var sock = new SockJS('/socket');
    stompClient = Stomp.over(sock);
    stompClient.connect({}, function (frame) {
        console.log('connected ' + frame);
        stompClient.subscribe('/sampleResult/data', function (data) {
            // 拿到数据后将字符串转成JSON对象
            samplerInfo = $.parseJSON(data.body);
            if (samplerInfo) {
                var timeStamp = samplerInfo["timeStamp"];
                var samplerLabel = samplerInfo["samplerLabel"];
                var samplerData = samplerInfo[dataType];

                var samplerPoint = {
                    name: samplerLabel,
                    value: [timeStamp, samplerData]
                };
                if (samplerFilter[samplerLabel] === undefined) {
                    samplerFilter[samplerLabel] = {
                        name: samplerLabel,
                        type: 'line',
                        showSymbol: false,
                        hoverAnimation: false,
                        data: samplerPoint
                    };
                    chart.setOption(samplerFilter[samplerLabel], true);
                } else {
                    samplerFilter[samplerLabel].push(samplerPoint);
                }
                samplerInfo = null;
            }
        });
    });
}

var chartId = $("input[name='chartId']").val();
var samplerChart = echarts.init(document.getElementById(chartId));

samplerChart.setOption({
    title: {
        text: $("input[name='chartTitle']").val()
    },
    tooltip: {
        trigger: 'axis',
        formatter: function () {
            return this.series.name
        },
        axisPointer: {
            animation: false
        }
    },
    xAxis: {
        type: 'time',
        splitLine: {
            show: false
        }
    },
    yAxis: {
        type: 'value',
        boundaryGap: [0, '100%'],
        splitLine: {
            show: false
        }
    },
    series: []
});

sampleResultSocket(samplerChart, $("input[name='dataType']").val());

