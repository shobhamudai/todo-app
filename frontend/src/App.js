import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'https://dctvfnojaf.execute-api.us-east-1.amazonaws.com/prod/todos';

const VERSION = '1.0.1'; // Change this version number to see your updates

function App() {
    const [todos, setTodos] = useState([]);
    const [task, setTask] = useState('');

    useEffect(() => {
        fetchTodos();
    }, []);

    const fetchTodos = () => {
        axios.get(API_URL).then(response => {
            console.log(response);
            setTodos(response.data);
        });
    };

    const addTodo = () => {
        axios.post(API_URL, { task }).then(response => {
            fetchTodos();
            setTask('');
        });
    };

    const toggleTodo = (id) => {
        const todo = todos.find(todo => todo.id === id);
        axios.put(`${API_URL}/${id}`, { ...todo, completed: !todo.completed }).then(response => {
            fetchTodos();
        });
    };

    const deleteTodo = (id) => {
        axios.delete(`${API_URL}/${id}`).then(() => {
            fetchTodos();
        });
    };

    const formatTimestamp = (epoch) => {
        if (!epoch) return null;
        // The backend is already providing milliseconds, so no multiplication is needed.
        return new Date(epoch).toUTCString();
    };

    return (
        <div className="container mt-5">
            {/* Version Number Display */}
            <div style={{ position: 'absolute', top: '10px', right: '10px', color: '#aaa', fontSize: '12px' }}>v{VERSION}</div>
            <h1>To-Do List</h1>
            <div className="input-group mb-3">
                <input
                    type="text"
                    className="form-control"
                    value={task}
                    onChange={(e) => setTask(e.target.value)}
                    placeholder="New task"
                    onKeyPress={(e) => {
                        if (e.key === 'Enter') {
                            addTodo();
                        }
                    }}
                />
                <div className="input-group-append">
                    <button className="btn btn-primary" onClick={addTodo}>Add</button>
                </div>
            </div>
            <ul className="list-group">
                {todos.map(todo => (
                    <li key={todo.id} className={`list-group-item d-flex justify-content-between align-items-center`}>
                        <div>
                            <span
                                style={{ textDecoration: todo.completed ? 'line-through' : 'none', cursor: 'pointer' }}
                                onClick={() => toggleTodo(todo.id)}
                            >
                                {todo.task}
                            </span>
                            {todo.createdAt && (
                                <div className="text-muted small">{formatTimestamp(todo.createdAt)}</div>
                            )}
                        </div>
                        <button className="btn btn-danger" onClick={() => deleteTodo(todo.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default App;
