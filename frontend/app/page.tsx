"use client"

import { useEffect, useState } from "react";

// 1. 데이터 타입 정의
interface Workout {
    id: number;
    exerciseName: string;
    weight: number;
    setNum: number;
    reps: number;
}

export default function Home() {
    const [workouts, setWorkouts] = useState<Workout[]>([]);

    // 2. 입력값을 담을 상태(State)들
    const [name, setName] = useState("");
    const [weight, setWeight] = useState(0);
    const [setNum, setSetNum] = useState(0);
    const [reps, setReps] = useState(0);

    // 3. 목록 가져오기 함수 (새로고침용)
    const fetchWorkouts = () => {
        fetch("http://localhost:8080/list")
            .then((res) => res.json())
            .then((data) => setWorkouts(data));
    };

    // 화면 켜지면 실행
    useEffect(() => {
        fetchWorkouts();
    }, []);

    // 4. 운동 추가하기 (POST)
    const addWorkout = () => {
        if (!name) return alert("운동 이름을 입력해주세요!");

        fetch("http://localhost:8080/api/workouts", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                name: name,     // 백엔드 DTO(WorkoutRequest)의 필드명과 맞춰야 함!
                weight: weight,
                setNum: setNum,
                reps: reps,
            }),
        }).then((res) => {
            if (res.ok) {
                alert("득근 성공! 💪");
                setName(""); // 입력창 비우기
                fetchWorkouts(); // 목록 다시 불러오기
            }
        });
    };

    // 5. 운동 삭제하기 (DELETE)
    const deleteWorkout = (id: number) => {
        if (!confirm("정말 삭제하시겠습니까?")) return;

        fetch(`http://localhost:8080/api/workouts/${id}`, {
            method: "DELETE",
        }).then((res) => {
            if (res.ok) {
                fetchWorkouts(); // 목록 갱신
            }
        });
    };

    return (
        <div className="min-h-screen p-8 bg-gray-100 text-gray-800">
            <h1 className="text-3xl font-bold mb-8 text-center text-blue-600">
                🏋️‍♂️ 영섭님의 득근 일지
            </h1>

            {/* 입력 폼 영역 */}
            <div className="max-w-2xl mx-auto bg-white p-6 rounded-lg shadow-md mb-8">
                <div className="grid grid-cols-2 gap-4 mb-4">
                    <input
                        type="text"
                        placeholder="운동 이름 (예: 스쿼트)"
                        className="border p-2 rounded col-span-2"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />
                    <input
                        type="number"
                        placeholder="무게 (kg)"
                        className="border p-2 rounded"
                        onChange={(e) => setWeight(Number(e.target.value))}
                    />
                    <div className="flex gap-2">
                        <input
                            type="number"
                            placeholder="세트"
                            className="border p-2 rounded w-full"
                            onChange={(e) => setSetNum(Number(e.target.value))}
                        />
                        <input
                            type="number"
                            placeholder="회"
                            className="border p-2 rounded w-full"
                            onChange={(e) => setReps(Number(e.target.value))}
                        />
                    </div>
                </div>
                <button
                    onClick={addWorkout}
                    className="w-full bg-blue-600 text-white p-3 rounded font-bold hover:bg-blue-700 transition"
                >
                    기록 추가하기 🔥
                </button>
            </div>

            {/* 리스트 영역 */}
            <div className="max-w-2xl mx-auto grid gap-4">
                {workouts.map((workout) => (
                    <div
                        key={workout.id}
                        className="bg-white p-6 rounded-lg shadow-md flex justify-between items-center"
                    >
                        <div>
                            <h2 className="text-xl font-bold text-gray-800">
                                {workout.exerciseName}
                            </h2>
                            <p className="text-gray-600 mt-1">
                                {workout.weight}kg · {workout.setNum}세트 · {workout.reps}회
                            </p>
                        </div>
                        <button
                            onClick={() => deleteWorkout(workout.id)}
                            className="bg-red-100 text-red-500 px-4 py-2 rounded hover:bg-red-200 transition"
                        >
                            삭제
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}