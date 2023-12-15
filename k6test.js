import http from "k6/http";
import { check } from "k6";

export const options = {
    vus: 10000,
    duration: "30s",
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
    const userSaveWithULID = "http://localhost:8080/test";
    const userSaveWithLong = "http://localhost:8080/test1";

    const response = http.post(userSaveWithULID, payload, params);
    check(response, {
        "success": (res) => res.status === 200,
    });
}