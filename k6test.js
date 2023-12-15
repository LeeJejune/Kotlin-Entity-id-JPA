import http from "k6/http";
import { check } from "k6";

export const options = {
    vus: 10000, // 가상 사용자 수를 10000명으로 변경
    duration: "30s", // 테스트 시간을 30초로 변경
};



export default function () {
    const payload = JSON.stringify({
        name: "test",
    });

    const params = {
        headers: {
            "Content-Type": "application/json",
        },
    };
    const url = "http://localhost:8080/test";
    const url2 = "http://localhost:8080/test1";

    const response = http.post(url, payload, params);
    check(response, {
        "success": (res) => res.status === 200,
    });
}