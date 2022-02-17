package ol.ko.packaloud

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ol.ko.packaloud.databinding.FragmentThemeListBinding

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */

class ThemeListFragment : Fragment() {

    private lateinit var binding: FragmentThemeListBinding
    private val viewModel by activityViewModels<ThemeListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThemeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Click Listener to trigger navigation based on if you have
        // a single pane layout or two pane layout
        val onItemClickListener: (String) -> Unit = { itemId ->
            // Leaving this not using view binding as it relies on if the view is visible the current
            // layout configuration (layout, layout-sw600dp)
            val itemDetailFragmentContainer: View? =
                view.findViewById(R.id.item_detail_nav_container)
            if (itemDetailFragmentContainer != null) {
                itemDetailFragmentContainer.findNavController()
                    .navigate(R.id.fragment_item_detail, ThemeDetailFragmentArgs(itemId).toBundle())
            } else {
                view.findNavController().navigate(ThemeListFragmentDirections.showItemDetail(itemId))
            }
        }

        val themesAdapter = GroupieAdapter()
        binding.itemList.adapter = themesAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val bookmarks = BookmarkRepository(BookmarkDataStore.getInstance(requireContext()))
                bookmarks.loadBookmark().combine(viewModel.packUi) { bookmarkRead, ui -> bookmarkRead to ui }.collect { (bookmarkRead, ui) ->
                    val bookmark = bookmarkRead ?: 0
                    Log.d("OLKO", "bookmark $bookmark")

                    (activity as AppCompatActivity?)?.supportActionBar?.title = ui?.pack?.title
                    // TODO tablet layout
                    binding.titleView?.text = ui?.pack?.title
                    binding.authorView?.text = ui?.pack?.author
                    binding.thanksView?.text = ui?.pack?.thanks

                    themesAdapter.clear()
                    var idx = 0
                    ui?.pack?.parts?.forEach { part ->
                        val section = Section()
                        section.setHeader(PartHeaderItem(part.name))
                        section.addAll(part.themes.map {
                            val themeIdx = idx++
                            ThemeItem(it, onItemClickListener, themeIdx == bookmark, {
                                lifecycleScope.launch {
                                    bookmarks.saveBookmark(themeIdx)
                                }
                            })
                        })
                        themesAdapter.add(section)
                    }
                }
            }
        }

        binding.hideButton?.setOnClickListener {
            val hide = (binding.hideButton?.text == getString(R.string.hide))
            binding.hideButton?.text = if (hide) getString(R.string.show) else getString(R.string.hide)
            binding.titleView?.visibility = if (hide) View.INVISIBLE else View.VISIBLE
            binding.authorView?.isVisible  = !hide
            binding.thanksView?.isVisible = !hide
        }
    }
}
