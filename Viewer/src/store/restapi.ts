import axios from "axios";

/* Fetch HLS Url From Backend */
export const fetchUrl = async (): Promise<any> => {
    try {
        const response = await axios.get('http://localhost:5002/rtsp/windows');
        return response.data.url;
    } catch (error) {
        console.error('HLS URL 불러오기 실패 : ', error);
        return;
    }
}