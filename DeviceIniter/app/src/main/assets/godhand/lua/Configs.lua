g_test_flag = false

g_conf_remote_ip = "192.168.70.97"
g_conf_remote_port = "3002"
g_conf_host = "http://"..g_conf_remote_ip..":"..g_conf_remote_port
g_conf_task_result_url = g_conf_host.."/task_status"
g_conf_task_file_url = g_conf_host.."/task_file"
g_conf_download_complete_url = g_conf_host.."/download_complete"
g_conf_check_update_url = g_conf_host.."/check_upgrade"
g_conf_update_url = g_conf_host.."/upgrade"
g_conf_get_vcode_url = g_conf_host.."/get_vcode"
g_conf_put_vcode_url = g_conf_host.."/put_vcode"
g_conf_send_phone_url = g_conf_host.."/send_phone"
g_conf_get_sms_url = g_conf_host.."/get_sms"
g_conf_upload_file_url = g_conf_host.."/upload"

g_conf_wx_vote_s_snapshot_path = "/sdcard/GodHand/tmp/vote_snapshot.png"