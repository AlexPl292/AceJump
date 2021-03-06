package org.acejump.session

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.editor.colors.EditorColors.CARET_COLOR
import com.intellij.util.containers.ContainerUtil
import it.unimi.dsi.fastutil.ints.IntArrayList
import org.acejump.*
import org.acejump.action.TagJumper
import org.acejump.action.TagVisitor
import org.acejump.boundaries.Boundaries
import org.acejump.boundaries.EditorOffsetCache
import org.acejump.boundaries.StandardBoundaries
import org.acejump.boundaries.StandardBoundaries.*
import org.acejump.config.AceConfig
import org.acejump.input.EditorKeyListener
import org.acejump.input.JumpMode
import org.acejump.input.JumpModeTracker
import org.acejump.input.KeyLayoutCache
import org.acejump.search.Pattern
import org.acejump.search.SearchProcessor
import org.acejump.search.Tagger
import org.acejump.search.TaggingResult
import org.acejump.view.TagCanvas
import org.acejump.view.TextHighlighter
import java.util.*

/**
 * Manages an AceJump session for a single [Editor].
 */
class Session(private val editor: Editor) {
  private val listeners: MutableList<AceJumpListener> =
    ContainerUtil.createLockFreeCopyOnWriteList()

  private companion object {
    private val defaultBoundaries
      get() = if (AceConfig.searchWholeFile) WHOLE_FILE else VISIBLE_ON_SCREEN
  }

  private val originalSettings = EditorSettings.setup(editor)

  private val jumpModeTracker = JumpModeTracker()
  private var jumpMode = JumpMode.DISABLED
    set(value) {
      field = value

      if (value === JumpMode.DISABLED) {
        end()
      } else {
        searchProcessor?.let { textHighlighter.render(it.results, it.query, jumpMode) }
        editor.colorsScheme.setColor(CARET_COLOR, value.caretColor)
        editor.contentComponent.repaint()
      }
    }

  private var searchProcessor: SearchProcessor? = null
  private var tagger = Tagger(editor)

  private val tagJumper
    get() = TagJumper(editor, jumpMode, searchProcessor)

  private val tagVisitor
    get() = searchProcessor?.let { TagVisitor(editor, it, tagJumper) }

  private val textHighlighter = TextHighlighter(editor)
  private val tagCanvas = TagCanvas(editor)

  @ExternalUsage
  val tags
    get() = tagger.tags

  init {
    KeyLayoutCache.ensureInitialized(AceConfig.settings)

    EditorKeyListener.attach(editor, object: TypedActionHandler {
      override fun execute(editor: Editor, charTyped: Char, context: DataContext) {
        var processor = searchProcessor
        val hadTags = tagger.hasTags

        if (processor == null) {
          processor = SearchProcessor.fromChar(
            editor,
            charTyped, defaultBoundaries
          ).also { searchProcessor = it }
        } else if (!processor.type(charTyped, tagger)) {
          return
        }

        updateSearch(
          processor, markImmediately = hadTags,
          shiftMode = charTyped.isUpperCase()
        )
      }
    })
  }

  /**
   * Updates text highlights and tag markers according to the current
   * search state. Dispatches jumps if the search query matches a tag.
   * If all tags are outside view, scrolls to the closest one.
   */
  private fun updateSearch(
    processor: SearchProcessor,
    markImmediately: Boolean,
    shiftMode: Boolean = false
  ) {
    val query = processor.query
    val results = processor.results

    textHighlighter.render(results, query, jumpMode)

    if (!markImmediately &&
      query.rawText.let {
        it.length < AceConfig.minQueryLength &&
          it.all(Char::isLetterOrDigit)
      }
    ) {
      return
    }

    when (val result = tagger.markOrJump(query, results.clone())) {
      is TaggingResult.Jump -> {
        tagJumper.jump(result.offset, shiftMode)
        tagCanvas.removeMarkers()
        end()
      }

      is TaggingResult.Mark -> {
        val tags = result.tags
        tagCanvas.setMarkers(tags)

        val cache = EditorOffsetCache.new()
        val boundaries = VISIBLE_ON_SCREEN

        if (tags.none {
            boundaries.isOffsetInside(editor, it.offsetL, cache) ||
              boundaries.isOffsetInside(editor, it.offsetR, cache)
          }) tagVisitor?.scrollToClosest()
      }
    }
  }

  @ExternalUsage
  fun markResults(resultsToMark: SortedSet<Int>) {
    tagger = Tagger(editor)
    tagCanvas.setMarkers(emptyList())

    val processor = SearchProcessor.fromRegex(editor, "", defaultBoundaries)
      .apply { results = IntArrayList(resultsToMark) }

    updateSearch(processor, markImmediately = true)
  }

  /**
   * Starts a regular expression search. If a search was already active,
   * it will be reset alongside its tags and highlights.
   */

  @ExternalUsage
  fun startRegexSearch(pattern: String, boundaries: Boundaries) {
    tagger = Tagger(editor)
    tagCanvas.setMarkers(emptyList())

    val processor = SearchProcessor.fromRegex(
      editor, pattern,
      boundaries.intersection(defaultBoundaries)
    ).also { searchProcessor = it }
    updateSearch(processor, markImmediately = true)
  }

  /**
   * Starts a regular expression search. If a search was already active,
   * it will be reset alongside its tags and highlights.
   */

  @ExternalUsage
  fun startRegexSearch(pattern: Pattern, boundaries: Boundaries) =
    startRegexSearch(pattern.regex, boundaries)

  /**
   * See [JumpModeTracker.cycle].
   */
  fun cycleNextJumpMode() {
    jumpMode = jumpModeTracker.cycle(forward = true)
  }

  /**
   * See [JumpModeTracker.cycle].
   */
  fun cyclePreviousJumpMode() {
    jumpMode = jumpModeTracker.cycle(forward = false)
  }

  /**
   * See [JumpModeTracker.toggle]
   */
  fun toggleJumpMode(newMode: JumpMode) {
    jumpMode = jumpModeTracker.toggle(newMode)
  }

  /**
   * See [TagVisitor.visitPrevious]. If there are no tags, nothing happens.
   */
  fun visitPreviousTag() =
    if (tagVisitor?.visitPrevious() == true) end() else Unit

  /**
   * See [TagVisitor.visitNext]. If there are no tags, nothing happens.
   */
  fun visitNextTag() = if (tagVisitor?.visitNext() == true) end() else Unit

  /**
   * Ends this session.
   */
  fun end() = SessionManager.end(editor)

  /**
   * Clears any currently active search, tags, and highlights.
   * Does not reset [JumpMode].
   */
  fun restart() {
    tagger = Tagger(editor)
    searchProcessor = null
    tagCanvas.removeMarkers()
    textHighlighter.reset()
  }

  /**
   * Should only be used from [SessionManager] to dispose a
   * successfully ended session.
   */
  internal fun dispose() {
    tagger = Tagger(editor)
    EditorKeyListener.detach(editor)
    tagCanvas.unbind()
    textHighlighter.reset()
    EditorCache.invalidate()
    listeners.forEach(AceJumpListener::finished)

    if (!editor.isDisposed) {
      originalSettings.restore(editor)
      editor.colorsScheme.setColor(CARET_COLOR, JumpMode.DISABLED.caretColor)
      editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
    }
  }

  @ExternalUsage
  fun addAceJumpListener(listener: AceJumpListener) {
    listeners += listener
  }

  @ExternalUsage
  fun removeAceJumpListener(listener: AceJumpListener) {
    listeners -= listener
  }
}
