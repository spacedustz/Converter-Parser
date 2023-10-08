import React, {useEffect, useRef, useState} from 'react';
import Hls from 'hls.js';
import {fetchUrl} from "../store/restapi"; // restapi.ts 파일을 import

const StreamingRtsp: React.FC = () => {
    const videoRef = useRef<HTMLVideoElement | null>(null);
    const [hlsUrl, setHlsUrl] = useState<string>('');

    // Backend에서 HLS URL GET
    const fetchHlsUrl = async () => {
        try {
            const url = await fetchUrl(); // fetchUrl 함수를 사용하여 HLS URL을 가져옴
            if (url) {
                setHlsUrl(url); // HLS URL을 상태 변수에 설정
            } else {
                console.error('HLS URL이 없습니다.');
            }
        } catch (error) {
            console.error('Failed to fetch HLS URL:', error);
        }
    };

    useEffect(() => {
        fetchHlsUrl(); // HLS URL을 가져옴
    }, []);

    useEffect(() => {
        if (videoRef.current && hlsUrl) {
            loadHlsStream(); // HLS 스트림을 로드
        }
    }, [hlsUrl]);

    const loadHlsStream = () => {
        let hls = null;
        if (Hls.isSupported()) {
            hls = new Hls();
            hls.loadSource(hlsUrl);
            hls.attachMedia(videoRef.current!);

            hls.on(Hls.Events.MANIFEST_PARSED, function () {
                videoRef.current!.play();
            });
        }

        return () => {
            if (hls) {
                hls.destroy();
            }
        };
    };

    return (
        <div>
            <video ref={videoRef} controls playsInline autoPlay/>
        </div>
    );
};

export default StreamingRtsp;