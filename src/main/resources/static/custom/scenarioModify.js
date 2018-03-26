var $modify_script_file = $(".file");
// 脚本同步上传
$modify_script_file.fileinput({
    maxFileCount: 1,
    language : "zh",
    showUpload: false,
    showRemove: true,
    overwriteInitial: true,                             // 覆盖同名文件
    uploadAsync: false
});

var $modify_scenario_button = $("#modify_scenario_button");
$modify_scenario_button.click(
    function() {
        var $modify_scenario_form = $("#modify_scenario_form");
        $modify_scenario_form.ajaxSubmit({
            url: "/modScenarioInfo",
            type: "post",
            success: function(result){
                var modifyScenarioResult = result["modifyScenarioResult"];
                var modifyScriptResult = result["modifyScriptResult"];
                var modifyParamResult = result["modifyParamResult"];

                if (modifyScenarioResult && modifyScriptResult) {
                    // 如果更新了脚本
                    var successMsg = "";
                    if (modifyParamResult.length > 0) {
                        successMsg = modifyScenarioResult.message + '<br/>' + modifyScriptResult.message + "<br/>" + "请重新上传参数文件";
                    } else {
                        successMsg = modifyScenarioResult.message + '<br/>' + modifyScriptResult.message;
                    }
                    bootbox.alert({
                        title: '提示',
                        message: successMsg,
                        callback: function () {
                            refreshParamsListView("/getScenarioCsvDataSetSlotList", result);
                            refreshScriptTreeView("/getScenarioScriptDataStructure", result);
                        }
                    });
                } else if (modifyScenarioResult && !modifyScriptResult) {
                    // 如果脚本没有被更新
                    bootbox.alert({
                        title: '提示',
                        message: '场景参数存储成功'
                    });
                } else {
                    // 如果遇到了其他问题
                    var errorMessages = result["paramErrors"];
                    if (errorMessages) {
                        var errMsg = '';
                        for (var index = 0; index < errorMessages.length; index++) {
                            errMsg = errMsg + errorMessages[index] + "<br/>";
                        }
                        bootbox.alert({
                            title: '警告',
                            message: errMsg
                        });
                    } else {
                        bootbox.alert({
                            title: '警告',
                            message: "不要重复提交场景"
                        });
                    }
                }
            },
            error: function() {
                bootbox.alert({
                    title: '警告',
                    message: "请求错误"
                });
            }
        });
    }
);

function refreshParamsListView(url, result) {
    $.ajax({
        url: url,
        type: "post",
        data: {
            scenarioId: result.scenarioId
        },
        success: function(result) {
            var $param_file_upload_list = $("#param_file_upload_list");
            $param_file_upload_list.empty();
            for (var index = 0; index < result.length; index++) {
                $param_file_upload_list.append("<div class='form-group'>\n" +
                                               "    <label for='csv_data_set_slot_"+index+"' class='col-sm-2 control-label'>"+result[index]["testElementName"]+"</label>\n" +
                                               "    <div class='col-sm-9'>\n" +
                                               "        <input name='csvDataSetName' type='hidden' value='"+result[index]["testElementName"]+"'/>\n" +
                                               "        <input id='csv_data_set_slot_"+index+"' name='paramFiles' type='file' class='file' data-show-preview='false' data-allowed-file-extensions='[\"csv\"]' data-msg-placeholder='"+result[index]["fileName"]+"'/>\n" +
                                               "    </div>\n" +
                                               "</div>");
            }
            var $modify_script_file = $(".file");
            $modify_script_file.fileinput({
                maxFileCount: 1,
                language : "zh",
                showUpload: false,
                showRemove: true,
                overwriteInitial: true,                             // 覆盖同名文件
                uploadAsync: false
            });
        },
        error: function() {
            bootbox.alert({
                title: '警告',
                message: '无法获取参数文件上传列表'
            });
        }
    });
}

function refreshScriptTreeView(url, result) {
    $.ajax({
        url: url,
        type: "post",
        data: {
            scenarioId: result.scenarioId
        },
        success: function(result) {
            var $script_tree = $("#modify_script_tree");
            $script_tree.treeview({
                data: result["scriptDataStructure"]
            });
            $script_tree.treeview('expandAll', {levels: 10, silent: true});
        },
        error: function() {
            bootbox.alert({
                title: '警告',
                message: '无法获取脚本结构'
            });
        }
    });
}

// 参数文件上传提交
var $modify_csv_data_set_button = $("#modify_csv_data_set_button");
$modify_csv_data_set_button.click(
    function() {
        var $modify_param_file_slot_list = $("#modify_param_file_slot_list");
        $modify_param_file_slot_list.ajaxSubmit({
            url: "/saveScenarioParamFiles",
            type: "post",
            success: function (result) {
                var status = result.status;
                var message = result.message;
                if (status === "Success") {
                    var insertParamFileMsg = "";
                    var insertParamFileMsgList = result["insertParamFileResultList"];
                    if (insertParamFileMsgList) {
                        // 如果脚本中存在CSV Data Set
                        for (var index = 0; index < insertParamFileMsgList.length; index++) {
                            insertParamFileMsg = insertParamFileMsg + insertParamFileMsgList[index].message + ": " + insertParamFileMsgList[index].object + "<br/>";
                        }
                        message = "场景创建完毕" + "<br/>" + message + "<br/>" + insertParamFileMsg;
                    } else {
                        // 如果脚本中不存在CSV Data Set
                        message = "场景创建完毕"
                    }
                    bootbox.alert({
                        title: "提示",
                        message: message,
                        callback: function () {
                            window.location.href = "/";             // 回到场景列表页
                        }
                    });
                } else if (status === "Fail") {
                    bootbox.alert({
                        title: "提示",
                        message: result.message
                    });
                }
            }
        });
    }
);


// 重置场景表单
var $clear_scenario_button = $("#clear_scenario_button");
$clear_scenario_button.click(
    function() {
        $('#modify_scenario_form').resetForm();
    }
);

// 重置参数表单
var $clear_csv_data_set_button = $("#clear_csv_data_set_button");
$clear_csv_data_set_button.click(
    function() {
        $('#modify_param_file_slot_list').resetForm();
    }
);


