import React, {useEffect, useRef} from 'react';
import Hls from 'hls.js';
import {fetchUrl} from "../store/restapi"; // restapi.ts 파일을 import

const StreamingRtsp: React.FC = () => {
    const videoRef = useRef<HTMLVideoElement | null>(null);
    const videoSrc = fetchUrl();
    const hls = new Hls();

    // Life Cycle Hooks
    useEffect(() => {
        loadHlsStream();
    }, []);

    const loadHlsStream = () => {

        if (Hls.isSupported()) {

            if (videoRef.current) {
                hls.loadSource(videoSrc);
                hls.attachMedia(videoRef.current!);
                hls.on(Hls.Events.MANIFEST_PARSED, function () {
                    videoRef.current.play();
                });
            }
        } else if (videoRef.current && videoRef.current?.canPlayType('application/vnd.apple.mpegurl')) {
            videoRef.current.src = videoSrc;
            videoRef.current?.addEventListener('loadedmetadata', function () {
                videoRef.current?.play();
            });
        }
    };

    return (
        <div>
            <video ref={videoRef} width={640} height={360} controls/>
        </div>
    );
};

export default StreamingRtsp;