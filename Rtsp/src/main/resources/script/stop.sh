#!/bin/bash

api_url="http://localhost:5002/api/hls/control"
api_ip="192.168.0.157"
api_command="stop"
api_key="abc"

# 카메라 정보를 배열로 정의
camera_info=(
    "1-260-01 8554"
    "1-260-04 8555"
    "1-294-01 8556"
    "1-294-02 8557"
    "1-414-02 8558"
    "1-414-03 8559"
    "1-438-02 8560"
    "1-465-01 8561"
    "1-465-04 8562"
)

# 배열 순회하면서 curl 호출
for i in "${!camera_info[@]}"; do
    camera_id=$((i+1))
    info=(${camera_info[i]})
    instance_name="${info[0]}"
    port="${info[1]}"

    # JSON 데이터를 변수로 생성
    json_data='{
        "cameraId": '$camera_id',
        "instanceName": "'$instance_name'",
        "ip": "'$api_ip'",
        "port": '$port',
        "command": "'$api_command'",
        "apiKey": "'$api_key'"
    }'

    # curl 호출
    curl -X POST -H "Content-Type: application/json" -d "$json_data" "$api_url" &
done

echo "HLS 변환 중지 성공"