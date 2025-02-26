# Shrinkwrap - PDF Compression Tool

A modern, Kotlin-based desktop application for compressing PDF files using Ghostscript. Built with Compose Multiplatform for a native desktop experience.

## 🚀 Features

- **Drag & Drop Interface**: Simply drag PDF files onto the application
- **Clipboard Support**: Paste files directly with ⌘+V (macOS)
- **Real-time Progress**: Visual feedback for compression status
- **Batch Processing**: Handle multiple files simultaneously
- **Compression Stats**: Shows file size reduction percentages
- **Finder Integration**: Click files to reveal in Finder

## 🏗️ Architecture

The application follows a clean, modular architecture designed for maintainability and testability:

```
src/main/kotlin/
├── Main.kt                     # Application entry point & UI orchestration
├── MainViewModel.kt            # State management & coordination
├── domain/                     # Business logic layer
│   ├── CompressionService.kt   # PDF compression interface & implementation
│   └── FileProcessingState.kt  # Domain models & state
├── infrastructure/             # External dependencies
│   └── CommandExecutor.kt      # System command execution abstraction
├── ui/                        # User interface components
│   ├── components/
│   │   ├── DragDropArea.kt     # Drag & drop functionality
│   │   └── FileListItem.kt     # File list item component
│   └── DashedBorder.kt         # UI utility for dashed borders
└── utils/                     # Common utilities
    └── FileUtils.kt           # File operations & formatting
```

### Key Architectural Principles

- **Separation of Concerns**: Domain, UI, and Infrastructure layers are clearly separated
- **Dependency Injection**: Services can be easily mocked and tested
- **Interface Segregation**: Clean interfaces enable easy testing and extension
- **Single Responsibility**: Each class has one clear purpose

## 🛠️ Getting Started

### Prerequisites

- **Kotlin/JVM**: Project uses Kotlin with Compose Desktop
- **Ghostscript**: Required for PDF compression functionality

  ```bash
  # macOS
  brew install ghostscript

  # Ubuntu/Debian
  sudo apt-get install ghostscript
  ```

### Building & Running

```bash
# Clone the repository
git clone <repository-url>
cd shrinkwrap

# Build the application
./gradlew build

# Run the application
./gradlew run

# Create native distribution
./gradlew createDistributable
```

## 🧪 Testing Strategy

The modular architecture enables comprehensive testing:

### Unit Tests

```kotlin
// Service layer testing
class CompressionServiceTest {
    @Test
    fun `should compress valid PDF file`() {
        val mockCommandExecutor = mockk<CommandExecutor>()
        val service = GhostscriptCompressionService(mockCommandExecutor)
        // Test without system dependencies
    }
}

// State management testing
class MainViewModelTest {
    @Test
    fun `should update state when file is added`() {
        val mockService = mockk<CompressionService>()
        val viewModel = MainViewModel(mockService)
        // Test state transitions
    }
}
```

### Integration Tests

- Test UI components with test state
- Test end-to-end file processing workflows
- Test error handling scenarios

## 🔮 Future Enhancements

### High Priority

#### 1. **Compression Quality Settings**

- Add UI controls for high/medium/low quality selection
- Remember user preferences
- Quality preview before compression

#### 2. **Enhanced Error Handling**

- User-friendly error messages
- Retry mechanisms for failed compressions
- Better validation feedback

#### 3. **Progress & Performance**

- Detailed progress bars for large files
- Compression speed metrics
- Estimated time remaining

### Medium Priority

#### 4. **Multiple Compression Backends**

```kotlin
// Easy to implement with current architecture
class PDFtkCompressionService : CompressionService { ... }
class ImageMagickCompressionService : CompressionService { ... }
```

#### 5. **Batch Operations**

- Select compression quality per batch
- Pause/resume batch processing
- Export compression reports

#### 6. **File Management**

- Original file backup options
- Custom output directory selection
- File name templates (e.g., `{original}_compressed.pdf`)

#### 7. **Advanced Features**

- PDF optimization beyond compression
- Image quality adjustments within PDFs
- Metadata preservation options

### Lower Priority

#### 8. **User Experience**

- Dark/light theme toggle
- Keyboard shortcuts for common actions
- Recent files list
- Undo/redo functionality

#### 9. **Analytics & Reporting**

- Compression statistics dashboard
- Export compression reports
- Historical data tracking

#### 10. **Cross-Platform Enhancements**

- Windows Explorer integration
- Linux file manager integration
- Platform-specific optimizations

### Technical Enhancements

#### 11. **Performance Optimizations**

- Memory-efficient processing for large files
- Background compression queue
- Multi-threaded processing improvements

#### 12. **Configuration System**

- Settings persistence
- Custom Ghostscript parameters
- Advanced user configurations

#### 13. **Plugin Architecture**

- Custom compression algorithms
- Third-party integrations
- Extension marketplace

## 🤝 Contributing

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions focused and small

### Testing Requirements

- Unit tests for all business logic
- UI tests for interactive components
- Integration tests for file operations
- Maintain >80% code coverage

### Architecture Guidelines

- Keep domain logic pure (no UI or infrastructure dependencies)
- Use dependency injection for testability
- Create reusable UI components
- Handle errors gracefully with meaningful messages

## 📋 Implementation Notes

### Adding New Compression Backends

1. Implement `CompressionService` interface
2. Add to dependency injection in `MainViewModel`
3. Add UI selection controls
4. Update tests

### Adding New UI Components

1. Create in `ui/components/` package
2. Follow existing component patterns
3. Make components reusable and configurable
4. Add preview functions for development

### Performance Considerations

- Large files should be processed in chunks
- UI should remain responsive during processing
- Memory usage should be monitored for batch operations

## 📝 License

[Add your license information here]

## 🙏 Acknowledgments

- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) for the UI framework
- [Ghostscript](https://www.ghostscript.com/) for PDF compression engine
