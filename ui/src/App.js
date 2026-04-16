import React, { useState, useEffect } from 'react';
import './index.css';

const API_URL = 'http://localhost:8081';

function App() {
  const [activeTab, setActiveTab] = useState('chapters');
  const [chapters, setChapters] = useState([]);
  const [progress, setProgress] = useState({});
  const [expandedChapters, setExpandedChapters] = useState({});
  const [sections, setSections] = useState({});
  const [flashcards, setFlashcards] = useState([]);
  const [currentCard, setCurrentCard] = useState(null);
  const [showAnswer, setShowAnswer] = useState(false);
  const [category, setCategory] = useState('fundamental');

  useEffect(() => {
    if (activeTab === 'chapters') {
      fetchChapters();
      fetchProgress();
    } else if (activeTab === 'flashcards') {
      fetchFlashcards();
    }
  }, [activeTab]);

  const fetchChapters = async () => {
    try {
      const response = await fetch(`${API_URL}/api/chapters`);
      const data = await response.json();
      setChapters(data);
    } catch (error) {
      console.error('Error fetching chapters:', error);
    }
  };

  const fetchProgress = async () => {
    try {
      const response = await fetch(`${API_URL}/api/progress`);
      const data = await response.json();
      const progressMap = {};
      data.forEach(p => {
        progressMap[p.sectionId] = p.completed;
      });
      setProgress(progressMap);
    } catch (error) {
      console.error('Error fetching progress:', error);
    }
  };

  const fetchFlashcards = async () => {
    try {
      const response = await fetch(`${API_URL}/api/flashcards?category=${category}`);
      const data = await response.json();
      setFlashcards(data);
      if (data.length > 0) {
        setCurrentCard(data[Math.floor(Math.random() * data.length)]);
      }
      setShowAnswer(false);
    } catch (error) {
      console.error('Error fetching flashcards:', error);
    }
  };

  const fetchSections = async (chapterId) => {
    try {
      const response = await fetch(`${API_URL}/api/chapters/${chapterId}/sections`);
      const data = await response.json();
      setSections(prev => ({ ...prev, [chapterId]: data }));
    } catch (error) {
      console.error('Error fetching sections:', error);
    }
  };

  const toggleChapter = (chapterId) => {
    setExpandedChapters(prev => {
      const newState = { ...prev, [chapterId]: !prev[chapterId] };
      if (newState[chapterId] && !sections[chapterId]) {
        fetchSections(chapterId);
      }
      return newState;
    });
  };

  const toggleSection = async (sectionId) => {
    const newCompleted = !progress[sectionId];
    try {
      await fetch(`${API_URL}/api/progress/${sectionId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ completed: newCompleted })
      });
      setProgress(prev => ({ ...prev, [sectionId]: newCompleted }));
    } catch (error) {
      console.error('Error updating progress:', error);
    }
  };

  const nextCard = () => {
    if (flashcards.length > 0) {
      setCurrentCard(flashcards[Math.floor(Math.random() * flashcards.length)]);
      setShowAnswer(false);
    }
  };

  const completedCount = Object.values(progress).filter(Boolean).length;
  const totalSections = chapters.length > 0 ? 60 : 0;

  return (
    <div className="app">
      <header>
        <h1>Claude Certified Architect</h1>
        <div className="tabs">
          <button className={activeTab === 'chapters' ? 'active' : ''} onClick={() => setActiveTab('chapters')}>Chapters</button>
          <button className={activeTab === 'flashcards' ? 'active' : ''} onClick={() => setActiveTab('flashcards')}>Flashcards</button>
        </div>
        {activeTab === 'chapters' && (
          <div className="progress-bar">
            <span>Progress: {completedCount}/{totalSections}</span>
            <div className="progress-fill" style={{ width: `${(completedCount / totalSections) * 100}%` }}></div>
          </div>
        )}
      </header>
      <main>
        {activeTab === 'chapters' && (
          <div className="chapters">
            {chapters.map(chapter => (
              <div key={chapter.id} className="chapter">
                <div className="chapter-header" onClick={() => toggleChapter(chapter.id)}>
                  <span className="chapter-icon">{expandedChapters[chapter.id] ? '▼' : '▶'}</span>
                  <span className="chapter-number">Chapter {chapter.id}</span>
                  <h2>{chapter.title}</h2>
                </div>
                {expandedChapters[chapter.id] && sections[chapter.id] && (
                  <div className="sections">
                    {sections[chapter.id].map(section => (
                      <div key={section.id} className={`section ${progress[section.id] ? 'completed' : ''}`}>
                        <label className="section-checkbox">
                          <input
                            type="checkbox"
                            checked={progress[section.id] || false}
                            onChange={() => toggleSection(section.id)}
                          />
                          <span className="section-title">{section.title}</span>
                        </label>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
        {activeTab === 'flashcards' && (
          <div className="flashcards">
            <div className="flashcard-controls">
              <select value={category} onChange={(e) => { setCategory(e.target.value); fetchFlashcards(); }}>
                <option value="fundamental">Fundamental (73)</option>
                <option value="foundations">Foundations (132)</option>
              </select>
              <button onClick={nextCard}>Next Card</button>
            </div>
            {currentCard && (
              <div className="flashcard" onClick={() => setShowAnswer(!showAnswer)}>
                <div className="flashcard-question">{currentCard.question}</div>
                {showAnswer && <div className="flashcard-answer">{currentCard.answer}</div>}
                {!showAnswer && <div className="flashcard-hint">Click to show answer</div>}
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
