import '@testing-library/jest-dom';

// IntersectionObserver n'existe pas dans jsdom
global.IntersectionObserver = class {
  constructor(callback) {}
  observe() {}
  unobserve() {}
  disconnect() {}
};
