package ol.ko.packaloud.ui

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ol.ko.packaloud.R
import ol.ko.packaloud.databinding.ItemHeaderBinding
import ol.ko.packaloud.databinding.ItemListContentBinding

data class ThemeItem(
    private val theme: PackTheme,
    private val onClickListener: (String) -> Unit,
    private val bookmarked: Boolean,
    val onBookmarkClickListener: View.OnClickListener
) : BindableItem<ItemListContentBinding>() {

    override fun getLayout(): Int = R.layout.item_list_content

    override fun bind(binding: ItemListContentBinding, position: Int) {
        binding.idText.text = theme.theme
        binding.bookmarkIcon.alpha = if (bookmarked) 1.0f else 0.25f
        if (!bookmarked) {
            binding.bookmarkIcon.setOnClickListener(onBookmarkClickListener)
        }
        binding.root.setOnClickListener { onClickListener(theme.theme) }
    }

    override fun initializeViewBinding(view: View): ItemListContentBinding = ItemListContentBinding.bind(view)
}

data class PartHeaderItem(
    private val partName: String
) : BindableItem<ItemHeaderBinding>() {

    override fun getLayout(): Int = R.layout.item_header

    override fun bind(binding: ItemHeaderBinding, position: Int) {
        binding.nameText.text = partName
    }

    override fun initializeViewBinding(view: View): ItemHeaderBinding = ItemHeaderBinding.bind(view)
}
