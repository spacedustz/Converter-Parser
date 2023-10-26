curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 1,
    "instanceName": "1-260-01",
    "ip": "192.168.0.122",
    "port": 8554,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 2,
    "instanceName": "1-260-04",
    "ip": "192.168.0.122",
    "port": 8555,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 3,
    "instanceName": "1-294-01",
    "ip": "192.168.0.122",
    "port": 8556,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 4,
    "instanceName": "1-294-02",
    "ip": "192.168.0.122",
    "port": 8557,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 5,
    "instanceName": "1-414-02",
    "ip": "192.168.0.122",
    "port": 8558,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 6,
    "instanceName": "1-414-03",
    "ip": "192.168.0.122",
    "port": 8559,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 7,
    "instanceName": "1-438-02",
    "ip": "192.168.0.122",
    "port": 8560,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 8,
    "instanceName": "1-465-01",
    "ip": "192.168.0.122",
    "port": 8561,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &


curl -X POST -H "Content-Type: application/json" -d '{
    "cameraId": 9,
    "instanceName": "1-465-04",
    "ip": "192.168.0.122",
    "port": 8562,
    "command": "start",
    "apiKey": "abc"
}' http://localhost:5002/api/hls/control &

echo "HLS 변환 요청 성공"