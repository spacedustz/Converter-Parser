import axios from "axios";

/* Fetch HLS Url From Backend */
export const fetchUrl = async (): Promise<string> => {
    try {
        return await axios.get('http://localhost:5002/videos/1/output.m3u8');
    } catch (error) {
        console.error('HLS URL 불러오기 실패 : ', error);
        return;
    }
}