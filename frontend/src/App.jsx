import { useState } from 'react';
import Editor from '@monaco-editor/react';

const API_BASE = 'http://localhost:8080/api';

function App() {
  const [problemStatement, setProblemStatement] = useState(
    'Given an array of integers, find two numbers that add up to a target.'
  );
  const [userCode, setUserCode] = useState('// Write your solution here');
  const [language, setLanguage] = useState('java');
  const [messages, setMessages] = useState([
    { kind: 'info', text: 'Hints and submit results appear here. Your code stays in the editor only.' }
  ]);
  const [discoverLoading, setDiscoverLoading] = useState(false);
  const [hintLoading, setHintLoading] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);

  const discoverProblem = async () => {
    if (!problemStatement.trim()) {
      setMessages((prev) => [
        ...prev,
        { kind: 'info', text: 'Type a query in the problem box (e.g. "leetcode 512", "dfs problem"), then click Generate.' }
      ]);
      return;
    }

    setDiscoverLoading(true);

    try {
      const response = await fetch(`${API_BASE}/discover`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ query: problemStatement, language })
      });

      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
      }

      const data = await response.json();
      if (data.problemStatement) setProblemStatement(data.problemStatement);
      if (data.boilerplateCode) setUserCode(data.boilerplateCode);

      setMessages((prev) => [
        ...prev,
        { kind: 'info', text: `Loaded a generated problem + ${language.toUpperCase()} boilerplate.` }
      ]);
    } catch (error) {
      setMessages((prev) => [
        ...prev,
        { kind: 'info', text: `Error generating problem: ${error.message}` }
      ]);
    } finally {
      setDiscoverLoading(false);
    }
  };

  const requestHint = async () => {
    if (!problemStatement.trim() || !userCode.trim()) {
      setMessages((prev) => [
        ...prev,
        { kind: 'info', text: 'Please provide both a problem statement and code in the editor.' }
      ]);
      return;
    }

    setHintLoading(true);

    try {
      const response = await fetch(`${API_BASE}/get-hint`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ problemStatement, userCode })
      });

      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
      }

      const data = await response.json();
      setMessages((prev) => [
        ...prev,
        { kind: 'hint', text: data.hint || 'No hint returned.' }
      ]);
    } catch (error) {
      setMessages((prev) => [
        ...prev,
        { kind: 'info', text: `Error fetching hint: ${error.message}` }
      ]);
    } finally {
      setHintLoading(false);
    }
  };

  const submitSolution = async () => {
    if (!problemStatement.trim() || !userCode.trim()) {
      setMessages((prev) => [
        ...prev,
        { kind: 'info', text: 'Please provide both a problem statement and code before submitting.' }
      ]);
      return;
    }

    setSubmitLoading(true);

    try {
      const response = await fetch(`${API_BASE}/submit`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ problemStatement, userCode })
      });

      if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
      }

      const data = await response.json();
      const verdict = data.correct ? 'Correct' : 'Incorrect';
      setMessages((prev) => [
        ...prev,
        {
          kind: 'submit',
          text: `${verdict}: ${data.message || 'No details.'}`
        }
      ]);
    } catch (error) {
      setMessages((prev) => [
        ...prev,
        { kind: 'info', text: `Error checking submission: ${error.message}` }
      ]);
    } finally {
      setSubmitLoading(false);
    }
  };

  const busy = discoverLoading || hintLoading || submitLoading;

  const monacoLanguage = language === 'cpp' ? 'cpp' : language;

  return (
    <div className="app-shell">
      <header className="topbar">
        <h1>DSA Hint Copilot</h1>
      </header>

      <main className="main-grid">
        <section className="editor-panel">
          <label className="label" htmlFor="problem-statement">
            Problem Statement
          </label>
          <textarea
            id="problem-statement"
            className="problem-input"
            value={problemStatement}
            onChange={(e) => setProblemStatement(e.target.value)}
            placeholder='Type a query like "leetcode 512", "dfs problem", or paste a full problem statement...'
          />

          <div className="language-row">
            <div className="language-left">
              <label className="label-inline" htmlFor="language">
                Language
              </label>
              <select
                id="language"
                className="language-select"
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                disabled={busy}
              >
                <option value="java">Java</option>
                <option value="python">Python</option>
                <option value="cpp">C++</option>
                <option value="javascript">JavaScript</option>
              </select>
            </div>

            <button className="generate-button" type="button" onClick={discoverProblem} disabled={busy}>
              {discoverLoading ? 'Generating...' : 'Generate'}
            </button>
          </div>

          <div className="editor-wrap">
            <Editor
              height="100%"
              language={monacoLanguage}
              value={userCode}
              onChange={(value) => setUserCode(value || '')}
              theme="vs-dark"
              options={{
                minimap: { enabled: false },
                fontSize: 14,
                wordWrap: 'on',
                smoothScrolling: true
              }}
            />
          </div>

          <div className="button-row">
            <button className="hint-button" type="button" onClick={requestHint} disabled={busy}>
              {hintLoading ? 'Generating Hint...' : 'Get AI Hint'}
            </button>
            <button className="submit-button" type="button" onClick={submitSolution} disabled={busy}>
              {submitLoading ? 'Checking...' : 'Submit'}
            </button>
          </div>
        </section>

        <section className="chat-panel">
          <div className="chat-header">Hint Chat</div>
          <div className="chat-messages">
            {messages.map((msg, index) => (
              <div
                key={`${msg.kind}-${index}`}
                className={`message assistant kind-${msg.kind}`}
              >
                <span className="role">
                  {msg.kind === 'submit' ? 'Check result' : msg.kind === 'info' ? 'Notice' : 'AI Mentor'}
                </span>
                <pre>{msg.text}</pre>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

export default App;
