// 设置bootbox中文
bootbox.setLocale('zh_CN');

// 搜索的下拉Select值保存
var $select_status = $("#select_status");
var $select_status_hidden = $("#select_status_hidden");
$select_status.selectpicker('val', $select_status_hidden.val());

// 执行查询
var $scenario_list = $("#scenario_list");
var $search_btn = $("#search_btn");
$search_btn.click(
    function() {
        $scenario_list.bootstrapTable("selectPage", 1);
    }
);

// 重置搜索条件
var $reset_btn = $("#reset_btn");
$reset_btn.click(
    function() {
        $('.form-group :text').val("");
        $select_status.selectpicker("val", "");
        $scenario_list.bootstrapTable('selectPage', 1);
    }
);

// 关闭模态框时刷新页面
var $add_script_modal = $("#add_scenario_modal");
$add_script_modal.on("hidden.bs.modal",
    function(){
        $("#upload_form").get(0).reset();                       // 通过$("#upload_form").get(0)获取原生DOM进行清空
        $scenario_list.bootstrapTable("refresh");               // 关闭模态框后刷新当前页面
    }
);

// 判断场景是否处于运行状态
var isActive = $("#scenario_is_active").val();
if (isActive !== undefined && isActive === "False") {
    bootbox.alert({
        title: '提示',
        message: '当前没有场景正在运行'
    });
}


// 修改场景
function modScenario(id) {
    $(window).attr('location', "/scenarioInfoMod?scenarioId=" + id);
}

// 删除场景
function delScenario(id) {
    bootbox.confirm({
        title: '提示',
        message: '确认删除场景?',
        callback: function(flag) {                              // 如果flag是true则代表确认删除
            if (flag) {
                $.ajax({
                    url: "/delScenarioInfo",
                    type: "post",
                    data : {
                        id: id
                    },
                    success: function(result) {
                        var delScenarioMsg = null;
                        // 从Controller返回来的Json
                        if (result["delScenarioResult"]) {
                            delScenarioMsg = result["delScenarioResult"].message;
                            delScenarioMsg = delScenarioMsg + "<br/>";
                        }
                        var delScriptMsg = null;
                        if (result["delScriptResult"]) {
                            delScriptMsg = result["delScriptResult"].message;
                            delScenarioMsg = delScenarioMsg + delScriptMsg + "<br/>";
                        }
                        var delParamsMsgList = null;
                        if (result["delParamFileResult"]) {
                            delParamsMsgList = result["delParamFileResult"];
                            if (delParamsMsgList) {
                                for (var i = 0; i < delParamsMsgList.length; ++i) {
                                    var delParamsMsg = delParamsMsgList[i].message + "<br/>";
                                    delScenarioMsg = delScenarioMsg + delParamsMsg;
                                }
                            } else {
                                delScenarioMsg = delScenarioMsg + "没有参数文件需要删除<br/>";
                            }
                        }
                        // 从Controller返回的Json
                        if (result["Error"] !== undefined) {
                            delScenarioMsg = result["Error"];
                        }
                        bootbox.alert({
                            title: '提示',
                            message: delScenarioMsg
                        });
                        $scenario_list.bootstrapTable("refresh");               // 关闭模态框后刷新当前页面
                    }
                });
            }
        }
    });
}

// 运行场景
function runScenario(scenarioId) {
    $.ajax({
        url: "/scenarioIsActive",
        type: "post",
        data : {
            scenarioId: scenarioId
        },
        success: function(result) {
            if (result["isActive"] === "true") {
                bootbox.alert({
                    title: '提示',
                    message: '场景' + result['scenarioName'] + '正在运行,请等待测试完成后再试.',
                    callback: function () {
                        $scenario_list.bootstrapTable('selectPage', 1);
                    }
                });
            } else {
                bootbox.confirm({
                    title: '提示',
                    message: '当前没有场景运行,开始执行测试.',
                    callback: function (isConfirm) {
                        // 如果在bootbox上点击的了确定,则执行测试.否则什么都不做.result为回调值
                        if (isConfirm === true)
                            $(window).attr('location', "/scenarioStartRun?scenarioId=" + scenarioId);
                    }
                });
            }
        }
    });
}

// 与Controller交互
// function performController(path, id) {
//     $.ajax({
//         url: path,
//         type: "post",
//         data : {
//             id: id
//         },
//         success: function(result) {
//             bootbox.alert({
//                 title: '提示',
//                 message: result.delScenarioStatus
//             });
//             $scenario_list.bootstrapTable('selectPage', 1);     // 在每次提交操作后返回首页
//         }
//     });
// }